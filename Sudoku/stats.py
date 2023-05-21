def calculate_mean_std(inputFilename: str):
    lines = open(f'./results/{inputFilename}', "r").readlines()
    counter = 0.000000000000000000
    results = 0.000000000000000000
    stepOne = []
    for i in lines:
        newI = i[:-2]
        tryDis = float(newI)
        results = results + tryDis
        counter += 1.000000000000000000
        stepOne.append(tryDis)

  # https://www.indeed.com/career-advice/career-development/how-to-calculate-standard-deviation
    mean = results/counter

    stepTwo = []
    for i in stepOne:
        x = pow(2, (i - mean))
        stepTwo.append(x)

    newCounter = 0.000000000000000000
    newResults = 0.000000000000000000
    for i in stepTwo:
        newResults += i
        newCounter += 1.000000000000000000

    newMean = newResults/newCounter
    std = sqrt(newMean)

    outputFilename = open(f'./results/statsYolo.txt', 'a')
    checkOutput = open(f'./results/statsYolo.txt', 'r').readlines()
    if len(checkOutput) == 0:
        outputFilename.write("Name of .txt file:\t\tMean\t\t\t\t\tSTD\n")
        outputFilename.write(inputFilename+"\t"+str(mean) +
                             "\t\t"+str(std)+"\n")
    else:
        if len(inputFilename) == 20:
            outputFilename.write(inputFilename+"\t" +
                                 str(mean) + "\t\t"+str(std)+"\n")
        if inputFilename == 'dlx_n=3.txt':
            outputFilename.write(inputFilename+"\t\t\t\t" +
                                 str(mean) + "\t\t" + str(std)+"\n")
        if len(inputFilename) == 11 and inputFilename != 'dlx_n=3.txt':
            outputFilename.write(inputFilename+"\t\t\t\t" +
                                 str(mean) + "\t\t"+str(std)+"\n")


            # benchmark('dlx', 5)
calculate_mean_std("backtracking_n=2.txt")
calculate_mean_std("backtracking_n=3.txt")
calculate_mean_std("backtracking_n=4.txt")
calculate_mean_std("dlx_n=2.txt")
calculate_mean_std("dlx_n=3.txt")
calculate_mean_std("dlx_n=4.txt")
calculate_mean_std("sat_n=2.txt")
calculate_mean_std("sat_n=3.txt")
calculate_mean_std("sat_n=4.txt")
