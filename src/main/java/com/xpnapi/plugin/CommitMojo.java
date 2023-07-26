package com.xpnapi.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

@Mojo(name = "deploy-commit", defaultPhase = LifecyclePhase.DEPLOY)
public class CommitMojo extends AbstractMojo {

    @Parameter(defaultValue = "${basedir}", required = true, readonly = true)
    private String baseDir;

    @Parameter(defaultValue = "${project.version}")
    private String version;

    @Parameter(defaultValue = "{version}")
    private String deployCommit;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            this.getLog().info("版本提交开始");
            this.getLog().info(this.baseDir);
            this.getLog().info(this.version);
            File file = new File(this.baseDir);
            try (Git git = Git.open(file)){
                git.add().addFilepattern(".").call();
                git.add().addFilepattern(".").setUpdate(true).call();
                git.commit().setMessage(this.deployCommit.replaceAll("\\{version}", this.version))
                        .setAll(true)
                        .setAllowEmpty(true)
                        .call();
            } catch (IOException | GitAPIException e) {
                throw new RuntimeException(e);
            }
            this.getLog().info("版本提交完成");
        } catch (Exception e) {
            this.getLog().warn(e);
            this.getLog().warn("版本提交失败");
        }
    }
}
