import java.util.Arrays;
import java.util.ListResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Function;

public class App_eo extends ListResourceBundle {

    String toCapital(String word) {
        String firstChar = word.charAt(0) + "";
        firstChar = firstChar.toUpperCase();
        return firstChar + word.substring(1);
    }

    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"", ""},
                {"it", "ĝi"},
                {"Good evening!", "Bonan vesperon!"},
                {"Good afternoon!", "Bonan posttagmezon!"},
                {"Good morning!", "Bonan matenon!"},
                {"Midnight now aren'you!", "Midnight now aren'you!"},
                {"It was good day!", "Estis bona tago!"},
                {"Good bye!", "Ĝis revido!"},
                {"See you again!", "Ĝis revido!"},
                {"I want to learn about animals.", "Mi volas lerni pri bestoj."},
                {"Which animal do you like most?", "Kiun beston vi plej ŝatas?"},
                {"Wonderful! I've learned so much about animals!","Mirinde! Mi lernis tiom multe pri bestoj!"},
                {"Welcome to the animal expert system!", "Bonvenon al la sperta sistemo de la besto!"},
                {"Welcome to the animal expert system!2", ""},
                {"What do you want to do:", "Kion vi volas fari:"},
                {"1. Play the guessing game", "1. Ludi la divenludon"},
                {"2. List of all animals", "2. Listo de ĉiuj bestoj"},
                {"3. Search for an animal", "3. Serĉi beston"},
                {"4. Calculate statistics", "4. Kalkuli statistikojn"},
                {"5. Print the Knowledge Tree", "5. Presu la scio arbo"},
                {"0. Exit", "0. Eliri"},
                {"Would you like to play again?", "Ĉu vi ŝatus ludi denove?"},
                {"You think of an animal, and I guess it.", "Vi pensu pri besto, kaj mi divenos ĝin."},
                {"Press enter when you're ready.", "Premu enen kiam vi pretas."},
                {"I give up. What animal do you have in mind?", "Mi rezignas. Kiun beston vi havas en la kapo?"},
                {"Come on, yes or no?", "Venu, jes aŭ ne?"},
                {"Please reply yes or no?", "Bonvolu respondi jes aŭ ne?"},
                {"You shuld say yes or no?", "Vi devas diri jes aŭ ne?"},
                {"The sentence should satisfy one of the following templates:", "La frazo devas kontentigi unu el la jenaj ŝablonoj:"},
                {"- It can ...", "- Ĝi povas ..."},
                {"- It has ...", "- Ĝi havas ..."},
                {"- It is a/an ...", "- Ĝi estas ..."},
                {"The sentence should be of the format: 'It can/has/is ...'.", "La frazo estu de la formato: 'Ĝi povas / havas / estas ...'."},
                {"examples of a statement.", "ekzemploj de aserto."},
                {"It is a mammal", "Ĝi estas mamulo"},
                {"It can barks", "Ĝi povas boji"},
                {"I have learned the following facts about animals:\n", "Mi lernis la jenajn faktojn pri bestoj:\n"},
                {"I can distinguish these animals by asking the question:\n", "Mi povas distingi ĉi tiujn bestojn per la demando:\n"},
                {"Nice! I've learned so much about animals!", "Bela! Mi lernis tiom multe pri bestoj!"},
                {"Here are the animals I know:", "Jen la bestoj, kiujn mi konas:"},
                {"Enter the animal:", "Enigu la beston:"},
                {"The Knowledge Tree stats", "La statistiko de la Scio-Arbo"},
                {" - root node\t\t\t", " - radika nodo\t\t\t"},
                {" - total number of nodes\t", " - tuta nombro de nodoj\t"},
                {" - total number of animals\t", " - totala nombro de bestoj\t"},
                {" - total number of statements\t" ," - nombro de deklaroj\t"},
                {" - height of the tree\t\t", " - alteco de la arbo\t\t"},
                {" - minimum depth\t\t", " - minimuma profundo\t\t"},
                {" - average depth\t\t", " - averaĝa profundo\t\t"},
                {"Specify a fact that distinguishes %s from %s.", (BiFunction<String, String, String>) (animal1, animal2) -> {
                    return String.format("Indiku fakton, kiu distingas %s de %s.", animal1, animal2);
                }},
                {"cv.askSV", (BiFunction<String, String, String>) (subject, verb) -> {
                    return "Ĉu " + subject + " " + verb;
                }},
                {"cv.askSVO", (Function<String[], String>) a -> {
                    String subject = a[0];
                    String verb = a[1];
                    String object = a[2];
                    return "Ĉu " + subject.toLowerCase() + " " + verb + " " + object + "?";
                }},
                {"cv.replyAnalize", (Function<String, String>) replyInput -> {
                    String reply = replyInput.toLowerCase();

                    if (reply.endsWith("..") ||
                            reply.endsWith("...") ||
                            reply.endsWith("!!") ||
                            reply.endsWith("!!!") ||
                            reply.endsWith(".!") ||
                            reply.endsWith("!.")) {

                        return "";
                    }

                    reply = reply.replace(".", "").replace("!", "");
                    long yesCount = Arrays.asList(reply.split("\\s+|,")).stream()
                            .filter(s ->
                                    "jes".equals(s) ||
                                            "suŝio".equals(s) ||
                                            "ĝuste".equals(s) ||
                                            "ĝusta".equals(s) ||
                                            "jesa".equals(s) ||
                                            "fakte".equals(s) ||
                                            "veto".equals(s) ||
                                            "diris".equals(s) ||
                                            "jes".equals(s))
                            .count();

                    long noCount = Arrays.asList(reply.split("\\s+")).stream()
                            .filter(s ->
                                    "de".equals(s) ||
                                            "ne".equals(s) ||
                                            "hmm".equals(s) ||
                                            "Nu".equals(s) ||
                                            "negativa".equals(s) ||
                                            "sumo".equals(s) ||
                                            s.matches(".*ne.*"))
                            .count();

                    if (noCount == 1) {
                        return "no";
                    }

                    if (yesCount == 1) {
                        return "yes";
                    }

                    return "";
                }},
                {"ac.getDifferenc.CheckAnswer", (Function<String, Boolean>) difference -> {
                    boolean moreFlag = false;
                    String[] words = difference.split("\\s+");
                    if (words.length > 2) {
                        String subject = words[0];
                        String verb = words[1];

                        if (!"ĝi".equals(subject)) {
                            moreFlag = true;
                        }
                        if (!"povas".equals(verb) && !"havas".equals(verb) && !"estas".equals(verb) && !"loĝas".equals(verb)) {
                            moreFlag = true;
                        }
                    } else {
                        moreFlag = true;
                    }
                    return moreFlag;
                }},
                {"Is the statement correct for %s?", (Function<String, String>) animal -> {
                    return String.format("Ĉu la aserto ĝustas por la %s?", animal);
                }},
                {"knowledge.affirmStatement", (Function<String, String[]>) affirmSentence -> {
                    String[] statement = new String[3];
                    String[] words = affirmSentence.split("\\s+");
                    String subject = words[0];
                    String verb = words[1];
                    int index = affirmSentence.indexOf(verb);
                    String fact = affirmSentence.substring(index);
                    fact = fact.replace(verb, "").trim();
                    if (fact.endsWith(".")) {
                        fact = fact.substring(0, fact.length() - 1);
                    }
                    statement[0] = subject;
                    statement[1] = verb;
                    statement[2] = fact;

                    return statement;
                }},
                {"knowledge.trueFact", (Function<String[], String>) SVO -> {
                    String subject = SVO[0];
                    String verb = SVO[1];
                    String fact = SVO[2];
                    String trueFact = "La " + subject + " " + verb + " " + fact + ".";
                    return trueFact;
                }},
                {"knowledge.falseFact", (Function<String[], String>) SVO -> {
                    String subject = SVO[0];
                    String denyVerb = SVO[1];
                    String fact = SVO[2];
                    String denyFact = "La " + subject + " " + denyVerb + " " + fact + ".";
                    return denyFact;
                }},
                {"cv.denyV", (Function<String, String>) verb -> {
                    return "ne " + verb;
                }},
                {"cv.denySV", (BiFunction<String, String, String>) (subject, denyVerb) -> {
                    return toCapital(subject) + " " + denyVerb;
                }},
                {"cv.affirmSVO", (Function<String[], String>) SVO -> {
                    String subject = SVO[0];
                    String verb = SVO[1];
                    String fact = SVO[2];
                    return toCapital(subject) + " " + verb + " " + fact + ".";
                }},
                {"cv.denySVO", (Function<String[], String>) SVO -> {
                    String subject = SVO[0];
                    String denyVerb = SVO[1];
                    String fact = SVO[2];
                    return toCapital(subject) + " " + denyVerb + " " + fact + ".";
                }},
                {"knowledge.animal", (Function<String, String[]>) animal -> {
                    String[] SVO = new String[3];
                    String subject = "ĝi";
                    String verb = "estas";
                    SVO[0] = subject;
                    SVO[1] = verb;
                    SVO[2] = animal;
                    return SVO;
                }},
                {"animal.new", (Function<String, String[]>) animal -> {
                    String name = animal;
                    String article = "";
                    String aname = animal;
                    String[] animalFields = new String[3];
                    animalFields[0] = name;
                    animalFields[1] = article;
                    animalFields[2] = aname;
                    return animalFields;
                }},
                {"Facts about the %s:", (Function<String, String>) animal -> {
                    return String.format("Faktoj pri la %s:", animal);
                }},
                {"No facts about the %s.", (Function<String, String>) animal -> {
                    return String.format("Neniuj faktoj pri la %s.", animal);
                }},
                {"animal.aname", (BiFunction<String, String, String>) (name, aname) -> name},
                {"animal.name", (BiFunction<String, String, String>) (name, aname) -> name}
        };
    }
}