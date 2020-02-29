package pl.pawelszopinski.result;

abstract class ArrayResultCompiler extends ResultCompiler {

    private final String[] items;
    private final String uriItemReplacement;

    ArrayResultCompiler(String uri, String uriItemReplacement,
                        String[] items, boolean authenticate) {
        super(uri, authenticate);
        this.items = items;
        this.uriItemReplacement = uriItemReplacement;
    }

    String[] getItems() {
        return items;
    }

    String getUriItemReplacement() {
        return uriItemReplacement;
    }
}
