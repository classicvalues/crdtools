package com.gs.crdtools;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.gs.crdtools.SourceGenFromSpec.toZip;

public class Generator {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: Generator GENERATED_SRC_ZIP CRD_YAML [CRD_YAML...]");
        }
        var crds = parseCrds(List.of(args).subSequence(1).map(Path::of));
        var result = generate(crds);
        toZip(result, Path.of(args[0]));
    }

    static List<Object> parseCrds(List<Path> inputs) {
        var parser = new Yaml();

        return inputs
                .map(p -> {
                    try {
                        return Files.readString(p, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(parser::loadAll);
    }

    static Map<Path, String> generate(List<Object> crds) throws IOException {
        var specs = SourceGenFromSpec.extractSpecs(crds);
        return SourceGenFromSpec.generateSourceCodeFromSpecs(specs);
    }

}