package com.ia.annotations;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.jooq.util.GenerationTool.generate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jooq.util.jaxb.Configuration;
import org.jooq.util.jaxb.Database;
import org.jooq.util.jaxb.Generator;
import org.jooq.util.jaxb.Jdbc;
import org.jooq.util.jaxb.Schema;
import org.jooq.util.jaxb.Target;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList.Builder;

public class CodeGenerator {

    private static final Joiner PIPE = on('|');

    public static void main(final String[] args) throws Exception {
        checkArgument(args.length == 1, "Exactly one argument (path to ia.properties) is required");

        /* Read the path to ia.properties file. */
        final String filePath = args[0];

        /* File must exist. */
        final File propertiesFile = new File(filePath);
        checkArgument(propertiesFile.isFile(), "File %s does not exist", filePath);

        /* Run the codeGenerator. */
        final CodeGenerator codeGenerator = new CodeGenerator(propertiesFile);
        codeGenerator.run();

    }

    private final ResourceBundle properties;

    private CodeGenerator(final File propertiesFile) {
        /* Create a resource bundle from the properties file. */
        properties = readResources(propertiesFile);

    }

    private Configuration createConfig() {
        return new Configuration().withJdbc(createJdbcConfig())
                .withGenerator(createGeneratorConfig());
    }

    private Database createDatabaseConfig() {
        return new Database()

                /* MySQL Database */
                .withName("org.jooq.util.mysql.MySQLDatabase")

                /* Table names to include */
                .withIncludes(includes())

                /* No exclusions */
                .withExcludes("")

                /* Schemas: plp */
                .withSchemata(new Schema().withInputSchema("plp"));
    }

    private Generator createGeneratorConfig() {
        return new Generator().withName("org.jooq.util.DefaultGenerator")
                .withDatabase(createDatabaseConfig()).withTarget(createTargetConfig());
    }

    private Jdbc createJdbcConfig() {
        return new Jdbc().withDriver(getProperty("ia.db.driver")).withUrl(getProperty("ia.db.url"))
                .withUser(getProperty("ia.db.user")).withPassword(getProperty("ia.db.password"));
    }

    private Target createTargetConfig() {
        return new Target().withPackageName("com.ia.generated").withDirectory(".");
    }

    private String getProperty(final String key) {
        return properties.getString(key);

    }

    private String includes() {
        return PIPE.join(tableNamePatterns());

    }

    private ResourceBundle readResources(final File propertiesFile) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(propertiesFile);
            return new PropertyResourceBundle(fis);

        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();

        }
        catch (final IOException e) {
            e.printStackTrace();

        }
        finally {
            closeQuietly(fis);
        }

        return null;

    }

    private void run() throws Exception {
        final Configuration configuration = createConfig();
        generate(configuration);
    }

    /* Add list of tables */

    private List<String> tableNamePatterns() {
        return new Builder<String>()

                .add("ia_organisations")

                .add("ia_ipaddress_for_organisation")

                .add("ia_transactions")

                .add("ia_user")

                .build();
    }
}
