package de.articdive.articdata.generators.v1_19.tags;

import de.articdive.articdata.datagen.annotations.NoGeneratorEntries;
import de.articdive.articdata.generators.v1_16_3.tags.StandardTagGenerator;
import de.articdive.articdata.generators.v1_19.common.Initializer;
import java.io.File;

@NoGeneratorEntries
public final class CatVariantTagGenerator extends StandardTagGenerator {
    public CatVariantTagGenerator() {
        super(new File(new File(Initializer.DATA_FOLDER_1_19, "tags"), "cat_variant"));
    }
}
