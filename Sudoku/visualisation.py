import matplotlib.pyplot as plt 
import statistics

algos = ['backtracking', 'sat', 'dlx']

def write_latex_tabulars():
  for algo in algos:
    tabular = open(f'results/{algo}_tabular.tex', 'w')
    tabular.write(r'\begin {tabular}{c|c|c}' + '\n')
    tabular.write('$n$ & Mean [s] & Standard Deviation [s]' + '\n')
    tabular.write(r'\\\hline' +  '\n')
    for n in range(2, 5):
      results = list(map(float, open(f'results/{algo}_n={n}.txt').readlines()))
      mu = statistics.mean(results)
      sd = statistics.stdev(results)
      tabular.write(f'{n} & {mu: .4f} & {sd: .4f}' + r'\\' + '\n')
    tabular.write(r'\end{tabular}' + '\n')

def plot_ns():
  ns = [2, 3, 4]
  for algo in algos:
    mus = []
    sds = []
    for n in ns:
      y = list(map(float, open(f'results/{algo}_n={n}.txt').readlines()))
      mus.append(statistics.mean(y))
      sds.append(statistics.stdev(y))
    plt.errorbar(ns, mus, sds, marker='o', capsize=3)
  plt.ylabel('Time [s]')
  plt.xlabel('Sudoku size $n$')
  plt.xticks(ns)
  plt.title('Mean and variance')
  plt.legend(['Backtracking', 'SAT', 'DLX'])
  plt.savefig('results/plot_ns')
  plt.show()
  
def subplot_ns():
  ns = [2, 3, 4]
  fig, axs = plt.subplots(3)
  for i, algo in enumerate(algos):
    mus = []
    sds = []
    for n in ns:
      y = list(map(float, open(f'results/{algo}_n={n}.txt').readlines()))
      mus.append(statistics.mean(y))
      sds.append(statistics.stdev(y))
    axs[i].errorbar(ns, mus, sds, marker='o', capsize=3)
    axs[i].set_title(algo)
    axs[i].set_xticks(ns)
  fig.suptitle('')
  fig.supylabel('Time [s]')
  fig.supxlabel('Sudoku size $n$')
  plt.savefig('results/subplot_ns')
  plt.show()

def plot_histograms():
  for n in range(2, 5):
    data = []
    for algo in algos:
      y = list(map(float, open(f'results/{algo}_n={n}.txt').readlines()))
      data.append(y)
    plt.figure(n-1)
    plt.boxplot(data, labels=algos, showfliers=False)
    plt.title(f'Histogram of the the three algorihms for $n$={n}')
    plt.savefig(f'results/histogram_n={n}')
    plt.show()

def subplot_histograms():
  fig, axs = plt.subplots(3)
  ns = range(2, 5)
  for n in ns:
    data = []
    for algo in algos:
      y = list(map(float, open(f'results/{algo}_n={n}.txt').readlines()))
      data.append(y)
    axs[n-2].boxplot(data, labels=algos)
    axs[n-2].set_title(f'$n$={n}')
  plt.savefig('results/subplot_histograms')
  plt.show()
  
# plot_ns()
# subplot_ns()
plot_histograms()
# subplot_histograms()