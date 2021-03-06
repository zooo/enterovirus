package com.gitenter.post_receive_hook.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.config.bean.GitSource;

@Configuration
public class GitSourceConfig {

	/*
	 * Can't set it up as dummy, because although we only write to the database,
	 * we access git through the domain layer (rather than where this application
	 * is) which needs this setup.
	 */
	
	/*
	 * TODO:
	 * This is duplicated to the one in web application (capsid). Also setup
	 * need to be done in both places. Error prone.
	 */
	@Profile("local")
	@Bean
	public GitSource stsGitSource() {
		return new GitSource(new File(System.getProperty("user.home"), "Workspace/gitenter-test/local-git-server"));
	}
	
	@Profile("docker")
	@Bean
	public GitSource dockerGitSource() {
		return new GitSource("/home/git");
	}

	@Profile("staging")
	@Bean
	public GitSource stagingGitSource() {
		return new GitSource("/home/git");
	}
	
	@Profile("production")
	@Bean
	public GitSource productionGitSource() {
		return new GitSource("/home/git");
	}
}
