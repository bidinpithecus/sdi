use rand::Rng;
use std::env;
use std::{
    collections::HashMap,
    net::{Ipv4Addr, SocketAddrV4, UdpSocket},
    sync::{Arc, Mutex},
    thread,
    time::Duration,
};

fn join_multicast_group(
    socket: &UdpSocket,
    multicast_group: Ipv4Addr,
    local_ip: Ipv4Addr,
) -> std::io::Result<()> {
    socket.join_multicast_v4(&multicast_group, &local_ip)
}

fn send_numbers(socket: UdpSocket, multicast_group: Ipv4Addr, port: u16, time_interval: u64) {
    thread::spawn(move || loop {
        let num = rand::thread_rng().gen_range(1..=10);
        let msg = num.to_string();
        println!("Enviando número: {}", msg);
        if let Err(e) = socket.send_to(msg.as_bytes(), &(multicast_group, port)) {
            eprintln!("Erro ao enviar mensagem: {}", e);
        }
        thread::sleep(Duration::from_secs(time_interval));
    });
}

fn receive_numbers(socket: UdpSocket, counts: Arc<Mutex<HashMap<u8, u32>>>) {
    thread::spawn(move || {
        let mut buf = [0; 10];
        loop {
            match socket.recv_from(&mut buf) {
                Ok((amt, _)) => {
                    let msg = String::from_utf8_lossy(&buf[..amt]);
                    if let Ok(num) = msg.trim().parse::<u8>() {
                        println!("Número recebido: {}", num);
                        let mut counts = counts.lock().unwrap();
                        *counts.entry(num).or_insert(0) += 1;
                    }
                }
                Err(e) => eprintln!("Erro ao receber mensagem: {}", e),
            }
        }
    });
}

fn report_most_frequent(counts: Arc<Mutex<HashMap<u8, u32>>>, time_interval: u64) {
    loop {
        thread::sleep(Duration::from_secs(time_interval));
        let counts = counts.lock().unwrap();
        if !counts.is_empty() {
            let (most_frequent_num, frequency) =
                counts.iter().max_by_key(|&(_, &count)| count).unwrap();
            println!(
                "Número mais frequente: {} com {} ocorrências",
                most_frequent_num, frequency
            );
        }
    }
}

fn main() -> std::io::Result<()> {
    let args: Vec<String> = env::args().collect();
    if args.len() != 4 {
        eprintln!("Uso: {} <ip_local> <multicast_group> <porta>", args[0]);
        return Ok(());
    }

    let local_ip = args[1].parse::<Ipv4Addr>().expect("IP local inválido");
    let multicast_group = args[2]
        .parse::<Ipv4Addr>()
        .expect("Grupo multicast inválido");
    let port: u16 = args[3].parse().expect("Porta inválida");

    let time_interval = 1;
    let report_interval = 2;

    let socket = UdpSocket::bind(SocketAddrV4::new(local_ip, port))?;

    join_multicast_group(&socket, multicast_group, local_ip)?;

    let counts = Arc::new(Mutex::new(HashMap::new()));

    let receive_socket = socket.try_clone()?;
    receive_numbers(receive_socket, Arc::clone(&counts));

    let send_socket = socket.try_clone()?;
    send_numbers(send_socket, multicast_group, port, time_interval);

    report_most_frequent(Arc::clone(&counts), report_interval);

    Ok(())
}
