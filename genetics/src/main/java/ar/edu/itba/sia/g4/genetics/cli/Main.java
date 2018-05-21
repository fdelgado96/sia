package ar.edu.itba.sia.g4.genetics.cli;

import ar.edu.itba.sia.g4.genetics.dnd.DNDCharacter;
import ar.edu.itba.sia.g4.genetics.dnd.Item;
import ar.edu.itba.sia.g4.genetics.dnd.Warrior1DNDCharacterSoup;
import ar.edu.itba.sia.g4.genetics.dnd.crossers.NilCrosser;
import ar.edu.itba.sia.g4.genetics.dnd.crossers.SinglePointCrosser;
import ar.edu.itba.sia.g4.genetics.dnd.mutators.NilMutator;
import ar.edu.itba.sia.g4.genetics.dnd.mutators.OneAlleleChoice;
import ar.edu.itba.sia.g4.genetics.dnd.mutators.OneAlleleMutator;
import ar.edu.itba.sia.g4.genetics.dnd.selectors.NilSelector;
import ar.edu.itba.sia.g4.genetics.dnd.targets.NilTarget;
import ar.edu.itba.sia.g4.genetics.engine.Darwin;
import ar.edu.itba.sia.g4.genetics.engine.problem.Crossover;
import ar.edu.itba.sia.g4.genetics.engine.problem.EvolutionaryTarget;
import ar.edu.itba.sia.g4.genetics.engine.problem.Mutator;
import ar.edu.itba.sia.g4.genetics.engine.problem.PrimordialSoup;
import ar.edu.itba.sia.g4.genetics.engine.problem.Selector;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.nio.file.Paths;
import java.util.List;

public class Main {

    private static CommandLineOptions parseArguments(String... args) {
        CommandLineOptions options = new CommandLineOptions();

        try {
            CmdLineParser parser = new CmdLineParser(options);
            parser.parseArgument(args);
            return options;
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        return null;
    }



    public static void main(String... args) {
        CommandLineOptions options = parseArguments(args);

        List<Item> gauntlets = ItemLoader.loadFromFile(Paths.get("items/guantes.tsv"));
        List<Item> helmets = ItemLoader.loadFromFile(Paths.get("items/cascos.tsv"));
        List<Item> boots = ItemLoader.loadFromFile(Paths.get("items/botas.tsv"));
        List<Item> weapons = ItemLoader.loadFromFile(Paths.get("items/armas.tsv"));
        List<Item> chestplates = ItemLoader.loadFromFile(Paths.get("items/pecheras.tsv"));

        PrimordialSoup<DNDCharacter> genesisPool = new Warrior1DNDCharacterSoup(10)
            .setBoots(boots)
            .setChestplates(chestplates)
            .setGauntlets(gauntlets)
            .setWeapons(weapons)
            .setHelmets(helmets);
        List<DNDCharacter> population = genesisPool.miracleOfLife();

//        Mutator<DNDCharacter> nilMutator = new NilMutator();
//        Crossover<DNDCharacter> nilCrosser = new NilCrosser();
        Selector<DNDCharacter> nilSelector = new NilSelector();
        EvolutionaryTarget<DNDCharacter> nilTarget = new NilTarget();
        Mutator<DNDCharacter> oneAlleleMutator = new OneAlleleMutator(new OneAlleleChoice() {
            @Override
            public double mutatationProb(long generation, int alleleIndex) {
                return 0;
            }
        }, (Warrior1DNDCharacterSoup) genesisPool);
        Crossover<DNDCharacter> singlePointCrosser = new SinglePointCrosser();

        Darwin<DNDCharacter> charles = new Darwin(oneAlleleMutator, singlePointCrosser, nilSelector, nilTarget);
        List<DNDCharacter> evolved = charles.evolve(population);

        evolved.forEach(p -> System.out.println(p));
    }
}