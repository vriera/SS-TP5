from functools import total_ordering
import json
from os import listdir
import numpy as np
import math
import sys
import re
import matplotlib.pyplot as plt




def main():
    runs = []
    
    probabilities = dict()
    
    names = ['10','15' , '20' , '25' , '30' , '35' , '40' , '45' , '50' ]
    for name in names:
        runs = parse("../results/", regex=f"PC_vz_{name}_") 
        for i in range(0,11):
                probabilities[i] = []
        for run in runs:
            pastMomentPos = None
            actualMomentPos = dict()
            for i in range(0,11):
                actualMomentPos[i] = dict()

            for moment in run:
                for idx , p in enumerate(moment):
                    if p[3] == 0:
                        r = np.sqrt(p[0] ** 2 + p[1] **2)
                        if( int(r) > 10):
                            actualMomentPos[10][idx] = (p[0],p[1])
                        else:
                            actualMomentPos[int(r)][idx] = (p[0],p[1])

                if pastMomentPos is not None:
                    for i in range (0,11):
                        quietos = 0
                        p_keys = list(pastMomentPos[i])
                        total = len(p_keys)
                        for key in p_keys:
                            if(key in actualMomentPos[i]):
                                if pastMomentPos[i][key] == actualMomentPos[i][key]:
                                    quietos+=1
                        if total != 0:
                            probabilities[i].append(quietos/total)

                pastMomentPos = actualMomentPos
                actualMomentPos = dict()
                for i in range(0,11):
                    actualMomentPos[i] = dict()


        p = [ np.average(x) for x in probabilities.values()]
        e = [ np.std(x)/np.sqrt(len(x)) for x in probabilities.values()]
        radius = [ f'{x}-{x+1}' for x in probabilities.keys()]
        plt.figure(figsize=(10, 5))
        ax = plt.gca()
        ax.set_ylim([0, 1])
        bars = plt.bar(radius , p , yerr=e , ecolor='black' , alpha=0.5 ,width = 0.7 , capsize=10)
        
        ax.bar_label(bars ,  fmt='%.2f' )
        plt.xlabel('Distancia al centro (m)')
        plt.ylabel('Probabilidad de permanecer quieto')
        plt.show()
            

             
      
















    

def parse(path , regex = '.*'):
    folders = [ folder for folder in listdir(path) if re.match(regex  , folder )]
    #folders = listdir(path)
    print(folders)
    runs = []
    for foldername in folders:
        with open(f'{path}/{foldername}/dynamic.json') as json_file:
            data = json.load(json_file)
            runs.append(data['t'])   
            json_file.close()
    return runs


if __name__ == '__main__':
    main()