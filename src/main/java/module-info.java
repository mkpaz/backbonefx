module backbonefx {

    requires javafx.base;
    requires javafx.graphics;

    requires transitive jakarta.inject;
    requires static org.jetbrains.annotations;

    exports backbonefx.di;
    exports backbonefx.event;
}
