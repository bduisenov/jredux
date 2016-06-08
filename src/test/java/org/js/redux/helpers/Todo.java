package org.js.redux.helpers;

/**
 * Created by bduisenov on 08/06/16.
 */
public class Todo {

    private final int id;

    private final String text;

    public Todo(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Todo todo = (Todo) o;

        if (id != todo.id)
            return false;
        return text != null ? text.equals(todo.text) : todo.text == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
