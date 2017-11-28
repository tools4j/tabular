package org.tools4j.launcher.service;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * User: ben
 * Date: 27/11/17
 * Time: 6:44 AM
 */
public class Utils {

    public static Predicate<Node> isEditable() {
        return evalToTrue(t -> ((TextField) t).isEditable());
    }

    public static Predicate<Node> evalToTrue(final Function<Node, Boolean> lambda) {
        return node -> lambda.apply(node);
    }

    public static Predicate<Node> not(Predicate<Node> predicate){
        return new Not(predicate);
    }

    public static Predicate<Node> containsText(String text) {
        return node -> containsNodeText(node, text);
    }

    private static boolean containsNodeText(Node node, String text) {
        if (node instanceof Labeled) {
            return ((Labeled) node).getText().contains(text);
        }
        else if (node instanceof TextInputControl) {
            return ((TextInputControl) node).getText().contains(text);
        }
        return false;
    }

    public static class Not implements Predicate<Node>{
        private final Predicate<Node> delegate;

        public Not(final Predicate<Node> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean test(final Node node) {
            return !delegate.test(node);
        }
    }
}
