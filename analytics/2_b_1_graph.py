import matplotlib.pyplot as plt
import json

def main():
    delta_t = 0.01875
    delta_t_cases = 6
    steps = delta_t_cases / delta_t

    max_t = 300
    max_step = max_t/delta_t
    big_step = (max_step // steps)


    #names = ['10' , '15' , '20' , '25' , '30' , '35' , '40' , '45' , '50'] 
    names = ['2', '10', '40', '80', '140', '200', '260', '320', '400', '500']
    for name in names:
        with open(f'{name}_data2.json') as data_file:
           # name_value = str(float(name) / 10) + ' m/s'
            name_value = name + ' humanos'
            data = json.load(data_file)
            avg = data[name]["avg"]
            err = data[name]["std_error"]
            x = [i * delta_t for i in range(0, int(steps * len(avg)), int(steps))]
            if (len(x) != len(avg)):
                print(f'{name}: X: {len(x)}, avg: {len(avg)}')
    
            # indices = [i for i, x in enumerate(avg) if x == 0]
            
            # for i in reversed(indices):
            #     print("ASD")
            #     avg.pop(i)
            #     err.pop(i)
            #     x.pop(i)

            plt.errorbar(x, avg,  yerr=err ,  marker=".", markersize=4, label=name_value , capsize=2 )
            
    plt.ylabel("Proporción de Zombies")
    plt.xlabel("Tiempo (s)")
    plt.legend(loc='upper right')
    plt.show()

    


if __name__ == "__main__":
    main()