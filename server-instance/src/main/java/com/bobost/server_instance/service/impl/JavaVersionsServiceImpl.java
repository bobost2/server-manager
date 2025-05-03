package com.bobost.server_instance.service.impl;

import com.bobost.server_instance.data.entity.InstanceConfig;
import com.bobost.server_instance.data.repository.InstanceConfigRepository;
import com.bobost.server_instance.dto.adoptium.ApiResponse;
import com.bobost.server_instance.dto.adoptium.BinaryInfo;
import com.bobost.server_instance.exception.adoptium_api.AdoptiumAPIException;
import com.bobost.server_instance.exception.adoptium_api.AdoptiumVersionMissingException;
import com.bobost.server_instance.exception.java.*;
import com.bobost.server_instance.service.JavaVersionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

@Service
public class JavaVersionsServiceImpl implements JavaVersionsService {

    InstanceConfigRepository instanceConfigRepository;

    public JavaVersionsServiceImpl(InstanceConfigRepository instanceConfigRepository) {
        this.instanceConfigRepository = instanceConfigRepository;
    }

    @Override
    public ArrayList<Integer> DownloadableJavaVersions() {
        // TODO: Can you fetch this instead of hardcoding it?
        return new ArrayList<>(
                List.of(8, 11, 16, 17, 18, 19, 20, 21, 22, 23, 24)
        );
    }

    @Override
    public Map<Integer, Boolean> GetInstalledJavaVersions() {
        Map<Integer, Boolean> javaVersions = new HashMap<>();

        // Put downloadable versions
        for (int version : DownloadableJavaVersions()) {
            javaVersions.put(version, false);
        }

        Path javaPath = Paths.get("./data/java");

        try {
            List<String> folderNames = new ArrayList<>();
            try (Stream<Path> stream = Files.list(javaPath)) {
                stream.filter(Files::isDirectory)
                        .map(path -> path.getFileName().toString())
                        .forEach(folderNames::add);
            }
            String[] folderArray = folderNames.toArray(new String[0]);

            for (String folder : folderArray) {
                // check if the folder name is a number
                if (folder.matches("\\d+")) {
                    int version = Integer.parseInt(folder);

                    if (Files.exists(javaPath.resolve(folder).resolve("bin").resolve("java"))) {
                        javaVersions.put(version, true);
                    }
                }
            }
        } catch (IOException e) {
            throw new JavaFolderAccessException(e.toString());
        }

        return javaVersions;
    }

    @Override
    public void DownloadJavaVersion(int version) {
        if (!DownloadableJavaVersions().contains(version)) {
            throw new JavaVersionMissingException("Java version " + version + " is not available for download.");
        }
        else if(GetInstalledJavaVersions().get(version)) {
            throw new JavaVersionAlreadyInstalledException("Java version " + version + " is already installed.");
        }

        String arch = System.getProperty("os.arch").toLowerCase();

        if (arch.contains("amd64") || arch.contains("x86_64")) {
            arch = "x64";
        } else if (arch.contains("arm64") || arch.contains("aarch64")) {
            arch = "aarch64";
        } else if (arch.contains("x86")) {
            arch = "x86";
        }

        System.out.println("[!] Searching JDKs for " + arch);

        String url = "https://api.adoptium.net/v3/assets/latest/" + version + "/hotspot?os=alpine-linux&architecture=" + arch + "&image_type=jdk";

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();

                ObjectMapper objectMapper = new ObjectMapper();
                ApiResponse[] apiResponse = objectMapper.readValue(responseBody, ApiResponse[].class);

                boolean foundJDK = false;

                for (ApiResponse actualResponse : apiResponse) {
                    BinaryInfo binaryInfo = actualResponse.getBinary();
                    if (binaryInfo.getImageType().equals("jdk")) {
                        String downloadUrl = binaryInfo.getPackageDetail().getLink();
                        System.out.println("Downloading JDK from URL: " + downloadUrl);

                        URI uri = URI.create(downloadUrl);
                        Path filePath = Paths.get("./data/java", "/jdk.tar.gz");
                        try (InputStream in = uri.toURL().openStream()) {
                            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        Path destDir = Paths.get("./data/java");
                        try (InputStream iStr = Files.newInputStream(filePath);
                             BufferedInputStream biStr = new BufferedInputStream(iStr);
                             GzipCompressorInputStream gzipStr = new GzipCompressorInputStream(biStr);
                             TarArchiveInputStream tarStr = new TarArchiveInputStream(gzipStr)) {

                            TarArchiveEntry entry;
                            while ((entry = tarStr.getNextEntry()) != null) {
                                // Construct full output path
                                Path outputPath = destDir.resolve(entry.getName());
                                if (entry.isDirectory()) {
                                    Files.createDirectories(outputPath);
                                } else {
                                    Files.createDirectories(outputPath.getParent());
                                    Files.copy(tarStr, outputPath, StandardCopyOption.REPLACE_EXISTING);
                                }
                            }

                            Files.deleteIfExists(filePath);
                        }

                        // find folder that starts with "jdk" and rename it to the version
                        String[] folderNames = destDir.toFile().list();

                        if (folderNames == null) {
                            throw new JavaVersionInstallationFailureException("No folders found in the java directory.");
                        }

                        for (String folder : folderNames) {
                            if (folder.startsWith("jdk")) {
                                Path oldPath = destDir.resolve(folder);
                                Path newPath = destDir.resolve(version + "");
                                Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                                break;
                            }
                        }

                        Path javaBinPath = destDir.resolve(version + "").resolve("bin").resolve("java").normalize();
                        //noinspection ResultOfMethodCallIgnored
                        javaBinPath.toFile().setExecutable(true);

                        foundJDK = true;
                        break;
                    }
                }

                if (!foundJDK) {
                    throw new AdoptiumVersionMissingException("JDK for version " + version
                            + " was not found for Alpine " + arch + " on the Adoptium API.");
                }
            } else {
                throw new AdoptiumAPIException("Error fetching JDK from Adoptium API - Error code " + response.statusCode());
            }
        } catch (AdoptiumAPIException | AdoptiumVersionMissingException e) {
            throw e;
        } catch (Exception e) {
            throw new JavaVersionInstallationFailureException(e.toString());
        }
    }

    @Override
    public void RemoveJavaVersion(int version) {
        InstanceConfig instanceConfig = instanceConfigRepository.findAll().getFirst();

        if (instanceConfig.getSelectedJavaVersion() == version) {
            throw new JavaVersionSelectedException("You cannot remove the Java version you are currently using.");
        }
        if (!GetInstalledJavaVersions().get(version)) {
            throw new JavaVersionNotInstalledException("Java version " + version + " is not installed.");
        }

        // Check if the version is selected too.

        Path javaPath = Paths.get("./data/java", version + "");
        try (Stream<Path> paths = Files.walk(javaPath)) {
            //noinspection ResultOfMethodCallIgnored
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new JavaVersionRemovalFailureException(e.toString());
        }
    }

    @Override
    public void SelectJavaVersion(int version) {
        InstanceConfig instanceConfig = instanceConfigRepository.findAll().getFirst();
        Map<Integer, Boolean> installedJavaVersions = GetInstalledJavaVersions();

        if (instanceConfig.getSelectedJavaVersion() == version) {
            throw new JavaVersionSelectedException("Java version " + version + " is already selected.");
        }

        if (!installedJavaVersions.get(version)) {
            throw new JavaVersionNotInstalledException("Java version " + version + " is not installed.");
        }

        instanceConfig.setSelectedJavaVersion(version);
        instanceConfigRepository.save(instanceConfig);
    }

    @Override
    public int GetSelectedJavaVersion() {
        InstanceConfig instanceConfig = instanceConfigRepository.findAll().getFirst();
        return instanceConfig.getSelectedJavaVersion();
    }

}
