from functools import total_ordering
import json
from os import listdir
import numpy as np
import math
import sys
import re


def main():
    runs = {}
    run_names = [sys.argv[1]] # ['2', '10', '40', '80', '140', '200']
    for name in run_names:
        runs[name] = parse("../results/", regex=f"PC_vz_{name}_") 
    print(runs.keys())

    delta_t = 0.01875
    delta_t_cases = 6
    steps = delta_t_cases / delta_t

    max_t = 300
    max_step = max_t/delta_t
    big_step = (max_step // steps)
    print(f"ASD: {big_step}")

    prob_per_name = {}
    for total_humans in runs:
        print(f'ASDASASD: {total_humans}')
        counter = 0
        prob_in_t_per_run = []
        for i in range(0, int(big_step)):
                prob_in_t_per_run.append(list())
        for run in runs[total_humans]:
            counter = 0
            ratios = []
            
            step = 0
            for instant_t in run:
                total_humans_in_t = 0
                total_zombies_in_t = 0
                counter = 0
                
                if (step % steps != 0 and instant_t != run[-1]):
                    step+=1
                    continue
                for particle in instant_t:
                    p_state = particle[3]
                    if p_state == 0:
                        total_humans_in_t += 1
                    else:
                        total_zombies_in_t += 1
                ratios.append((step, total_humans_in_t, total_zombies_in_t))
                step += 1
            # print(ratios)
            for s, h, z in ratios:
                index = s // steps
                # print(f"Step {s} zomb: {z}")
                prob_in_t_per_run[int(index)].append(z/(h+z))
        block_avg = []
        block_std = []
        for block in prob_in_t_per_run:
            if (len(block) <= 0):
                block_avg.append(0);
                block_std.append(0);
            else:
                block_avg.append(np.average(block))
                block_std.append((np.std(block))/math.sqrt(len(block)))
        # print(prob_in_t_per_run)
        print(block_std)
        prob_per_name[total_humans] = {}
        prob_per_name[total_humans]['avg'] = block_avg
        prob_per_name[total_humans]['std_error'] = block_std

    with open(f'{total_humans}_C_data.json', 'w+') as file:
        json.dump(prob_per_name, file)


                
    # for name in runs.keys():
    #     run = runs[name]
    #     run_ratios = []
    #     ratios = []
    #     step = 0
    #     for t in run:
    #         # if (step % steps != 0 and step < (len(run) - 1)):
    #         #     step += 1
    #         #     continue
            
    #         for particles in t:
    #             total_zombies = 0
    #             total_humans = 0
    #             for particle in particles:
    #                 p_state = particle[3]
    #                 if p_state == 0:
    #                     total_humans += 1
    #                 else:
    #                     total_zombies += 1
                       
    #         ratios.append((step, total_humans, total_zombies))
    #         step += 1
    #         # Aca tenemos la cantidad de cada tipo
    #         run_ratios.append(ratios)
    #     print(run_ratios)




    



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