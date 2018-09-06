package jp.kusumotolab.finergit;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

public class FinerGitConfig {

  private Path srcPath;
  private Path desPath;
  private String headCommitId;
  private boolean isOriginalJavaIncluded;
  private boolean isOtherFilesIncluded;
  private boolean isTokenized;
  private boolean isAccessModifierIncluded;
  private boolean isReturnTypeIncluded;

  public FinerGitConfig() {
    this.srcPath = null;
    this.desPath = null;
    this.headCommitId = null;
    this.isOriginalJavaIncluded = false;
    this.isOtherFilesIncluded = false;
    this.isTokenized = true;
    this.isAccessModifierIncluded = true;
    this.isReturnTypeIncluded = true;
  }

  public Path getSrcPath() {
    return this.srcPath;
  }

  public Path getDesPath() {
    return this.desPath;
  }

  public String getHeadCommitId() {
    return this.headCommitId;
  }

  public boolean isOriginalJavaIncluded() {
    return this.isOriginalJavaIncluded;
  }

  public boolean isOtherFilesIncluded() {
    return this.isOtherFilesIncluded;
  }

  public boolean isTokenized() {
    return this.isTokenized;
  }

  public boolean isAccessModifierIncluded() {
    return this.isAccessModifierIncluded;
  }

  public boolean isReturnTypeIncluded() {
    return this.isReturnTypeIncluded;
  }

  @Option(name = "-s", required = true, aliases = "--src", metaVar = "<path>",
      usage = "path to input repository")
  public void setSrcPath(final String path) {
    this.srcPath = Paths.get(path)
        .toAbsolutePath();
  }

  @Option(name = "-d", required = true, aliases = "--des", metaVar = "<path>",
      usage = "path to output repository")
  public void setDesPath(final String path) {
    this.desPath = Paths.get(path)
        .toAbsolutePath();
  }


  @Option(name = "--head", metaVar = "<commitId>", usage = "commitId for HEAD of finer repository")
  public void setEndCommit(final String endCommitId) {
    this.headCommitId = endCommitId;
  }

  @Option(name = "-o", aliases = "--original-javafiles", metaVar = "<true|false>)",
      usage = "finer repository includes whether original Java files or not")
  public void setOriginalJavaIncluded(final String flag) {
    switch (flag.toLowerCase()) {
      case "true": {
        this.isOriginalJavaIncluded = true;
        break;
      }
      case "false": {
        this.isOriginalJavaIncluded = false;
        break;
      }
      default: {
        System.err.println("\"-o\" option can take only true or false");
        System.exit(0);
      }
    }
  }

  @Option(name = "-p", aliases = "--otherfiles", metaVar = "<true|false>)",
      usage = "finer repository includes whether other files or not")
  public void setOtherFilesIncluded(final String flag) {
    switch (flag.toLowerCase()) {
      case "true": {
        this.isOtherFilesIncluded = true;
        break;
      }
      case "false": {
        this.isOtherFilesIncluded = false;
        break;
      }
      default: {
        System.err.println("\"-p\" option can take only true or false");
        System.exit(0);
      }
    }
  }

  @Option(name = "-t", aliases = "--tokenize", metaVar = "<true|false>)",
      usage = "do tokenize Java method files")
  public void setTokenized(final String flag) {
    switch (flag.toLowerCase()) {
      case "true": {
        this.isTokenized = true;
        break;
      }
      case "false": {
        this.isTokenized = false;
        break;
      }
      default: {
        System.err.println("\"-t\" option can take only true or false");
        System.exit(0);
      }
    }
  }

  @Option(name = "-l", aliases = "--log-level", metaVar = "<level>",
      usage = "log level (trace, debug, info, warn, error)")
  public void setLogLevel(final String logLevel) {
    final ch.qos.logback.classic.Logger log =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    switch (logLevel.toLowerCase()) {
      case "trace": {
        log.setLevel(Level.TRACE);
        break;
      }
      case "debug": {
        log.setLevel(Level.DEBUG);
        break;
      }
      case "info": {
        log.setLevel(Level.INFO);
        break;
      }
      case "warn": {
        log.setLevel(Level.WARN);
        break;
      }
      case "error": {
        log.setLevel(Level.ERROR);
        break;
      }
      default: {
        System.err.println("inappropriate value for \"-l\" option");
        System.exit(0);
      }
    }
  }

  @Option(name = "--access-modifier-included", metaVar = "<true|false>)",
      usage = "include access modifiers in Java method files")
  public void setAccessModifierIncluded(final String flag) {
    switch (flag.toLowerCase()) {
      case "true": {
        this.isAccessModifierIncluded = true;
        break;
      }
      case "false": {
        this.isAccessModifierIncluded = false;
        break;
      }
      default: {
        System.err.println("\"--access-modifier-included\" option can take only true or false");
        System.exit(0);
      }
    }
  }

  @Option(name = "--return-type-included", metaVar = "<true|false>)",
      usage = "include return types in Java method files")
  public void setReturnTypeIncluded(final String flag) {
    switch (flag.toLowerCase()) {
      case "true": {
        this.isReturnTypeIncluded = true;
        break;
      }
      case "false": {
        this.isReturnTypeIncluded = false;
        break;
      }
      default: {
        System.err.println("\"--return-type-included\" option can take only true or false");
        System.exit(0);
      }
    }
  }
}
