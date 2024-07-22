package dev.lukebemish.letmemixinplease;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class LetMeMixinPleasePlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        // So that this runs after the target extensions are added
        removeEmbeddiumEnforcement();
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    private static final Logger LOGGER = LogManager.getLogger(LetMeMixinPleasePlugin.class);

    private static final MethodHandle EXTENSIONS_GETTER;
    private static final MethodHandle ACTIVE_EXTENSIONS_GETTER;
    private static final MethodHandle ACTIVE_EXTENSIONS_SETTER;

    static {
        try {
            var lookup = MethodHandles.lookup();
            var extensionsLookup = MethodHandles.privateLookupIn(Extensions.class, lookup);
            EXTENSIONS_GETTER = extensionsLookup.findGetter(Extensions.class, "extensions", List.class);
            ACTIVE_EXTENSIONS_GETTER = extensionsLookup.findGetter(Extensions.class, "activeExtensions", List.class);
            ACTIVE_EXTENSIONS_SETTER = extensionsLookup.findSetter(Extensions.class, "activeExtensions", List.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void removeEmbeddiumEnforcement() {
        var extensionsObject = (Extensions) ((IMixinTransformer) MixinEnvironment.getDefaultEnvironment().getActiveTransformer()).getExtensions();
        try {
            var extensions = (List<IExtension>) EXTENSIONS_GETTER.invoke(extensionsObject);
            var activeExtensions = new ArrayList<>((List<IExtension>) ACTIVE_EXTENSIONS_GETTER.invoke(extensionsObject));
            Predicate<IExtension> toRemove = it -> {
                String name = it.getClass().getName();
                return name.contains("embeddium_integrity") || name.contains("embeddium.taint");
            };
            extensions.removeIf(toRemove);
            activeExtensions.removeIf(toRemove);
            ACTIVE_EXTENSIONS_SETTER.invoke(extensionsObject, Collections.unmodifiableList(activeExtensions));
        } catch(Throwable e) {
            LOGGER.error(e);
        }
    }
}
