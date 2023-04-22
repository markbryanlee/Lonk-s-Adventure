package com.ge.action;

import com.ge.general.ApplicationWindow;

import java.util.Random;

public class ActionItemRead extends Action{
    @Override
    public String execute() {
        if (getSubject().equalsIgnoreCase("Letter")){
            return helgasLetter();
        } else if (getSubject().equalsIgnoreCase("Book")){
            return holyBook();
        }

        return "This item is not readable.";
    }

    private String helgasLetter(){
        String contents =
                "Great Kingdom of Kourend\n\n" +
                "This letter hereby confirms that Lonk is is part of our allied forces.\n\n" +
                "(Oil Stamp)\n\n" +
                "(Signature) Chief Commander of the Queens troops."
                ;
        return contents;
    }

    private String holyBook(){
        String[] pages = new String[]{
                "Does a bird fall in a snare on the earth,\n" +
                        "when there is no trap for it?\n" +
                        "Does a snare spring up from the ground,\n" +
                        "when it has taken nothing?\n" +
                        "Is a trumpet blown in a city,\n" +
                        "and the people are not afraid?",

                "Then the men of Ephraim said to him, “What is this that you have done to us, not to call us when you went to fight against Midian?”\n" +
                        "And they accused him fiercely. And he said to them, “What have I done now in comparison with you?\n" +
                        "Is not the gleaning of the grapes of Ephraim better than the grape harvest of Abiezer?",

                "Then they shall take some of the blood and put it on the two doorposts and the lintel of the houses in which they eat it.\n" +
                        "They shall eat the flesh that night, roasted on the fire; with unleavened bread and bitter herbs they shall eat it.\n" +
                        "Do not eat any of it raw or boiled in water, but roasted, its head with its legs and its inner parts.",

                "Woe to those who devise wickedness\n" +
                        "and work evil on their beds!\n" +
                        "When the morning dawns, they perform it,\n" +
                        "because it is in the power of their hand.\n" +
                        "They covet fields and seize them,\n" +
                        "and houses, and take them away;\n" +
                        "they oppress a man and his house,\n" +
                        "a man and his inheritance.",

                "On my bed by night\n" +
                        "I sought him whom my soul loves;\n" +
                        "I sought him, but found him not.\n" +
                        "I will rise now and go about the city,\n" +
                        "in the streets and in the squares;\n" +
                        "I will seek him whom my soul loves.\n" +
                        "I sought him, but found him not.\n" +
                        "The watchmen found me\n" +
                        "as they went about in the city.\n" +
                        "“Have you seen him whom my soul loves?”",

                "If there is famine in the land, if there is pestilence or blight or mildew or locust or caterpillar,\n" +
                        "if their enemy besieges them in the land at their gates, whatever plague, whatever sickness there is,\n" +
                        "whatever prayer, whatever plea is made by any man or by all your people, each knowing\n" +
                        "the affliction of his own heart and stretching out his hands toward this house,\n" +
                        "then hear in heaven your dwelling place and light his soul on fire to each whose heart you know,\n" +
                        "according to all his ways so that he won’t feel emptiness no longer."
        };
        Random rand = new Random();
        int page = rand.nextInt(0, pages.length);

        StringBuilder sb = new StringBuilder();
        sb.append("You don't have the time to read the entire book so you decide to open it at a random page.\n");
        sb.append(String.format("Page: %d reads:\n\n", (page+1) * 123));
        sb.append(pages[page]);

        return sb.toString();
    }
}
