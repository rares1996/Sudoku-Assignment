from subprocess import Popen, PIPE
from time import time

def read_sudokus(n):
  sudokus = []
  file = open(f'./sudokus/sudokus_n={n}.txt', 'r')
  for _ in range(100):
    sudoku = str(n)+'\n'
    for _ in range(n*n):
        sudoku += file.readline()
    sudokus.append(sudoku)
    file.readline()
  return sudokus

def run_java(jar, algo, input):
  prog = Popen(['java', '-jar', jar, algo], stdin=PIPE, stdout=PIPE)
  out, _ = prog.communicate(input.encode('utf-8'))
  return out.decode('utf-8')

def run_kissat(res: str):
  prog = Popen(['../../kissat/build/kissat'], stdin=PIPE, stdout=PIPE)
  out, _ = prog.communicate(res.encode('utf-8'))
  return out.decode('utf-8')

def benchmark(algo, n):
  file = open(f'./results/{algo}_n={n}.txt', 'w')
  count = 0
  for sudoku in read_sudokus(n):
    count += 1
    timer = time()
    res = run_java('app/build/libs/app.jar', algo, sudoku)
    # print(res)
    if algo == 'sat':
        run_kissat(res)
        # print(res)
    timer = time() - timer
    print(count, timer)
    file.write(str(timer) + '\n')
  print(algo + ' n=' + str(n) + ' done')


for n in range(2, 5):
  benchmark('backtracking', n)
  benchmark('dlx', n)
  benchmark('sat', n)