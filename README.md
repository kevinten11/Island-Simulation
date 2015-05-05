# Island-Simulation
Simulation of Birds Evolving on an Island


To run a simulation, make a call like this in the Driver:

Simulator.runSims(Simulator.SeedType.UNIFORM, Simulator.MateType.CHOOSY_CLOSE, Simulator.MemoryType.NONE, 1000, 1);

- you will need an /Out folder with /Agg, /Analysis, and /Raw sub-folders

A Breif Explanation of modes:

  1) Seed Type:
    UNIFORM: Gives the generated seeds a uniform distribution of sizes between 1.0 and 9.0
    BIMODAL: Gives the generated seeds a bimodal distribution with means at 7.5 and 2.5 and StDs of 1.0
    NORMAL: GIves the generated seeds a normal distribution with mean at 5.0 and StD of 2.0
    
    Note: These values are subject to change and are variables in the Seed class
    
  2) MatType:
    RANDOM:
    CHOOSY_PRECISE:
    CHOOSY_CLOSE:
    
    

