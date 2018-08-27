package jp.kusumotolab.finergit.sv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.finergit.util.LinkedHashMapSorter;

public class SemanticVersioningMain {

  private static final Logger log = LoggerFactory.getLogger(SemanticVersioningMain.class);

  public static void main(final String[] args) {
    log.info("enter main(String[])");

    final SemanticVersioningConfig config = new SemanticVersioningConfig();
    final CmdLineParser cmdLineParser = new CmdLineParser(config);

    try {
      cmdLineParser.parseArgument(args);
    } catch (final CmdLineException e) {
      cmdLineParser.printUsage(System.err);
      log.info("exit main(String[])");
      System.exit(0);
    }

    final List<String> otherArguments = config.getOtherArguments();

    if (0 == otherArguments.size()) {
      System.err.println("target file is not specified");
      log.info("exit main(String[])");
      System.exit(0);
    }

    if (1 < otherArguments.size()) {
      System.err.println("two or more target files are specified");
      log.info("exit main(String[])");
      System.exit(0);
    }

    final String targetFile = otherArguments.get(0);
    final Path targetFilePath = Paths.get(targetFile);

    if (targetFilePath.isAbsolute()) {
      System.err.println("target file must be specified with a relative path");
      log.info("exit main(String[])");
      System.exit(0);
    }

    final String baseDir = config.getBaseDir();
    final Path baseDirPath = Paths.get(baseDir);

    final Path targetFileAbsolutePath = baseDirPath.resolve(targetFilePath);

    if (!Files.exists(targetFileAbsolutePath)) {
      System.err.println("\"" + targetFileAbsolutePath.toString() + "\" does not exist.");
      log.info("exit main(String[])");
      System.exit(0);
    }

    else if (!Files.isRegularFile(targetFileAbsolutePath)) {
      System.err.println("\"" + targetFileAbsolutePath.toString() + "\" is not a regular file.");
      log.info("exit main(String[])");
      System.exit(0);
    }

    config.setTargetFilePath(targetFilePath);

    final SemanticVersioningMain main = new SemanticVersioningMain(config);
    main.run();

    log.info("exit main(String[])");
  }

  private final SemanticVersioningConfig config;

  public SemanticVersioningMain(final SemanticVersioningConfig config) {
    log.info("enter SemanticVersionMain(SemanticVersioningConfig)");
    this.config = config;
  }

  public void run() {
    log.info("enter run()");
    final Path baseDirPath = Paths.get(this.config.getBaseDir());
    final Repository repository = findRepository(baseDirPath);

    if (null == repository) {
      System.err.println("git repository was not found.");
      log.info("exit run()");
      System.exit(0);
    }

    final Path targetFilePath = this.config.getTargetFilePath();
    final Path targetFileAbsolutePath = baseDirPath.resolve(targetFilePath);
    final Path targetFileRelativePathInRepository =
        this.getRelativePath(repository, targetFileAbsolutePath);

    final FileTracker fileTracker = new FileTracker(repository);
    final LinkedHashMap<RevCommit, String> commitPathMap =
        fileTracker.exec(targetFileRelativePathInRepository.toString());

    if (commitPathMap.isEmpty()) {
      System.err.println("there is no commit on \"" + targetFilePath.toString() + "\"");
      log.info("exit run()");
      System.exit(0);
    }

    final LinkedHashMap<RevCommit, String> reversedCommitPathMap =
        LinkedHashMapSorter.reverse(commitPathMap);

    final SemanticVersionGenerator semanticVersionGenerator = new SemanticVersionGenerator();
    final SemanticVersion semanticVersion = semanticVersionGenerator.exec(reversedCommitPathMap);

    if (this.config.isFollow()) {

      final List<SemanticVersion> semanticVersions = semanticVersion.getAllSemanticVersions();
      if (!this.config.isReverse()) {
        Collections.reverse(semanticVersions);
      }

      semanticVersions.stream()
          .map(s -> s.toString(this.config))
          .forEach(System.out::println);
    }

    else {
      System.out.println(semanticVersion.toString(this.config));
    }

    log.info("exit run()");
  }

  private Repository findRepository(final Path path) {
    log.trace("enter findRepository(Path), path <{}>", path);

    if (null == path) {
      return null;
    }

    final Path gitConfigPath = path.resolve(".git");
    if (Files.isDirectory(gitConfigPath)) {
      try {
        return new FileRepository(gitConfigPath.toFile());
      } catch (final IOException e) {
        log.error("A FileRepository object cannot be created for {}", gitConfigPath.toFile());
        return null;
      }
    }

    else {
      return findRepository(path.getParent());
    }
  }

  private Path getRelativePath(final Repository repository, final Path targetFileAbsolutePath) {
    log.trace(
        "enter getRelativePath(Repository, Path), repository <{}>, targetFileAbsolutePath <{}>",
        repository.getWorkTree()
            .getAbsolutePath(),
        targetFileAbsolutePath);
    final Path repositoryAbsolutePath = Paths.get(repository.getWorkTree()
        .getAbsolutePath());
    return repositoryAbsolutePath.relativize(targetFileAbsolutePath);
  }
}