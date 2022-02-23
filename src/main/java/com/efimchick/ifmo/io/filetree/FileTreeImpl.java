package com.efimchick.ifmo.io.filetree;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class FileTreeImpl implements FileTree {

    @Override
    public Optional<String> tree(Path path) {
        File file = new File(String.valueOf(path));
        if (!file.exists()) return Optional.empty();
        if (file.isFile()) {
            return Optional.of(file.getName() + " " + file.length() + " bytes");
        }
        if (file.isDirectory()) {

            return Optional.of(directoryTree(file, new ArrayList<>(), 0));
        }
        return Optional.empty();
    }

    private String directoryTree(File folder, List<Boolean> lastFolders, int level) {
        StringBuilder directory = new StringBuilder();
        if (lastFolders.size() != 0) {
            for (int i = 0; i < level; i++) {
                if (folder.isFile()) {
                    directory.append("│  ");
                }
            }

            directory.append(!(lastFolders.get(lastFolders.size() - 1)) ? "├─ " : "└─ ");
        }

        directory.append(folder.getName()).append(" ").append(folderSize(folder));

        File[] files = folder.listFiles();
        assert files != null;

        int count = files.length;
        files = sortFiles(files);


        for (int i = 0; i < count; i++) {
            directory.append("\n");
            for (Boolean lastFolder : lastFolders) {
                if (lastFolder) {
                    directory.append("   ");
                } else {
                    directory.append("│  ");
                }
            }
            if (files[i].isFile()) {
                directory.append(i + 1 == count ? "└" : "├").append("─ ").append(files[i].getName()).append(" ").append(files[i].length()).append(" bytes");
            } else {
                ArrayList<Boolean> list = new ArrayList<>(lastFolders);
                list.add(i + 1 == count);
                directory.append(directoryTree(files[i], list, level + 1));
            }
        }
        return directory.toString();
    }

    private long getFolderSize(File folder) {
        long size = 0;
        File[] files = folder.listFiles();

        assert files != null;

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            } else {
                size += getFolderSize(file);
            }
        }
        return size;
    }

    private String folderSize(File folder) {
        return getFolderSize(folder) + " bytes";
    }

    private File[] sortFiles(File[] incomeData) {

        List<File> files = new ArrayList<>();

        List<File> directories = new ArrayList<>();
        for (File file : incomeData) {
            if (file.isDirectory()) directories.add(file);
            else files.add(file);
        }

        directories.sort((o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
        files.sort((o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));

        directories.addAll(files);

        return directories.toArray(new File[directories.size()]);
    }
}