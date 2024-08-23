import multiprocessing
import socket
import struct
import time
import random
import matplotlib.pyplot as plt
from collections import defaultdict

MULTICAST_GROUP = '224.1.1.1'
MULTICAST_PORT = 42069
SEND_INTERVAL = 2
RUN_DURATION = 30 # Tempo de execução em segundos

frequency_counter = defaultdict(int)

def start_multicast_receiver(group, port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind(('', port))

    mreq = struct.pack('4sl', socket.inet_aton(group), socket.INADDR_ANY)
    sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
    sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_LOOP, 1)

    return sock

def send_random_number(sock, group, port):
    number = random.randint(1, 10)
    message = f'Número: {number}'
    sock.sendto(message.encode('utf-8'), (group, port))
    print(f'Número enviado: {number}')

def receive_and_tally(sock, timeout):
    sock.settimeout(timeout)
    local_counter = defaultdict(int)

    try:
        while True:
            data, _ = sock.recvfrom(1024)
            _, number = data.decode('utf-8').split(': ')
            number = int(number)
            local_counter[number] += 1
            print(f'Número recebido: {number}')
    except socket.timeout:
        pass

    return local_counter

def run_multicast_process(interval, duration, group, port):
    sock = start_multicast_receiver(group, port)
    start_time = time.time()

    while time.time() - start_time < duration:
        send_random_number(sock, group, port)
        time.sleep(interval)

        received_counter = receive_and_tally(sock, interval)
        if received_counter:
            for number, count in received_counter.items():
                frequency_counter[number] += count

            most_common = max(frequency_counter, key=frequency_counter.get)
            print(f'Número mais comum até agora: {most_common} com {frequency_counter[most_common]} ocorrências')
        else:
            print('Nenhum número recebido nesta iteração.')

    print('Processo finalizado após o tempo limite.')

    plot_histogram()

def plot_histogram():
    numbers = list(frequency_counter.keys())
    counts = list(frequency_counter.values())

    plt.bar(numbers, counts)
    plt.xlabel('Número')
    plt.ylabel('Frequência')
    plt.title('Histograma de Frequência')
    plt.savefig('histograma.png')
    plt.close()

if __name__ == '__main__':
    run_multicast_process(SEND_INTERVAL, RUN_DURATION, MULTICAST_GROUP, MULTICAST_PORT)
