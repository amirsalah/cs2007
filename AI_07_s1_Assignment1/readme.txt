[Operators available]:
the selection methods: tournament and roulette_wheel.
the crossover methods: single_point and partially_matched.

[Recommended configuration]
For small grid size, such as 8 and 12, both tournament and roulette wheel seletion method work well.
For large grid size, such as 24, 36 and 64, the tournament selection is recommended.
the tournament size is recommended at gridSize/2, such as gridSize=24, tournamentSize=12
(setting tournamentSize is explained in the next section).

[Other parameters]
Tournament size: is fixed in the program, which is 10 by default.
You can change the tournament size to vary the selection pressure.
Increasing the tournament size will also increase the selection pressure, vice versa.

Selection pressure: you can also vary the selection pressure by changing the variable selPressure,
which is in the function "evaluateFitness()".

[Experiments]
roulette wheel selection + single point crossover works badly for large sized boards.
e.g. Generally, it will take about 40 minutes to find a solution.

The roulette wheel selection + partially matched crossover works fine for large sized boards.
e.g. Generally, it will take about 6 minutes to find a solution.

the tournament selection works fine for large sized boards, regardless of the crossover method.
but the tournament size should be adjusted for different sizes for better performance.
e.g. grid size = 64, tournamentSize=20. in average, a solution can be found in 5 minutes.
