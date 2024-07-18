package com.jahirtrap.healthindicator.util.configlib;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("unchecked")
public abstract class TXFConfig {
    public static class EntryInit {
        boolean centered;
        Object defaultValue;
    }

    public static final Map<String, Class<? extends TXFConfig>> configClass = new HashMap<>();
    public static Path path;

    public static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).excludeFieldsWithModifiers(Modifier.PRIVATE).addSerializationExclusionStrategy(new HiddenAnnotationExclusionStrategy()).setPrettyPrinting().create();

    public static void init(String modid, Class<? extends TXFConfig> config) {
        path = FMLPaths.CONFIGDIR.get().resolve(modid + ".json");
        configClass.put(modid, config);

        for (Field field : config.getFields()) {
            EntryInit init = new EntryInit();
            if (field.isAnnotationPresent(Comment.class)) init.centered = field.getAnnotation(Comment.class).centered();
            if (field.isAnnotationPresent(Entry.class))
                try {
                    init.defaultValue = field.get(null);
                } catch (IllegalAccessException ignored) {}
            if ((field.isAnnotationPresent(Entry.class) || field.isAnnotationPresent(Comment.class)) && !field.isAnnotationPresent(Server.class) && !field.isAnnotationPresent(Hidden.class) && (FMLEnvironment.dist.isClient()))
                TXFConfigClient.initClient(modid, field, init);
        }
        try { gson.fromJson(Files.newBufferedReader(path), config); }
        catch (Exception e) { write(modid); }
    }

    public static TXFConfig getClass(String modid) {
        try { return configClass.get(modid).getDeclaredConstructor().newInstance(); } catch (Exception e) {throw new RuntimeException(e);}
    }
    public static void write(String modid) {
        getClass(modid).writeChanges(modid);
    }

    public void writeChanges(String modid) {
        path = FMLPaths.CONFIGDIR.get().resolve(modid + ".json");
        try {
            if (!Files.exists(path)) Files.createFile(path);
            Files.write(path, gson.toJson(getClass(modid)).getBytes());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Entry {
        int width() default 100;
        double min() default Double.MIN_NORMAL;
        double max() default Double.MAX_VALUE;
        String name() default "";
        boolean isColor() default false;
        boolean isSlider() default false;
        int precision() default 100;
        String category() default "default";
    }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Client {}
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Server {}
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Hidden {}
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Comment {
        boolean centered() default false;
        String category() default "default";
    }

    public static class HiddenAnnotationExclusionStrategy implements ExclusionStrategy {
        public boolean shouldSkipClass(Class<?> clazz) { return false; }
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getAnnotation(Entry.class) == null;
        }
    }
}
