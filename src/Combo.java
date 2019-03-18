import java.util.LinkedList;

public class Combo {
    private int[] combo;

    public Combo(int[] combo) {
        this. combo = new int[combo.length];
        System.arraycopy(combo, 0, this.combo, 0, combo.length);
    }

    public boolean contains(int r) {
        for (int i :
                combo) {
            if (i == r) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(int n) {
        for (int i = 0; i < combo.length; i++) {
            if (n == combo[i]) {
                return i;
            }
        }
        return -1;
    }

    public void substitute(int index, int n) {
        combo[index] = n;
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int a :
                combo) {
            builder.append(a);
        }
        return builder.toString();
    }

    public int[] getCombo() {
        return combo;
    }
}
