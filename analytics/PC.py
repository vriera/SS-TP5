import numpy as np
import matplotlib.pylab as plt



def main():
    runs = []
    
    probabilities = dict()
    

       
    for run in runs:
        pastMomentPos = None
        actualMomentPos = dict()
        for i in range(0,11):
            actualMomentPos[i] = []
        for moment in run:
            for p in moment:
                if p[3] == 0:
                    r = np.sqrt(p[0] ** 2 + p[1] **2)
                    actualMomentPos[int(r)].append((p[0],p[1]))

            if pastMomentPos is not None:
                for i in range (0,11):
                    franja = zip(pastMomentPos[i],actualMomentPos[i])
                    total = len(franja[i])
                    quietos = 0
                    
                    for pair in franja:
                        if pair[0] == pair[1]:
                            quietos+=1
                    
                    if total != 0:
                        probabilities[i].append(quietos/total)
            pastMomentPos = actualMomentPos
            actualMomentPos = dict()
            for i in range(0,11):
                actualMomentPos[i] = []


        p = [ np.average(x) for x in probabilities.values()]
        e = [ np.std(x) for x in probabilities.values()]
        radius = [ f'{x}-{x+1}' for x in probabilities.keys()]
        plt.bar(p, radius , width = 0.1)
        plt.xlabel('distancia al centro (m)')
        plt.ylabel('probabilidad de movimiento')
        plt.show()
            