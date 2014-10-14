package de.codecentric.jbehave.admin.services;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

@ConfigurationProperties(prefix = "jbehave")
public class SvnStoryImporter implements StoryImporter {

	private static final Log LOGGER = LogFactory.getLog(SvnStoryImporter.class);

	private String localStoryPath;
	private String remoteStoryPath;
	private String remoteUser;
	private String remotePassword;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PostConstruct
	public void importStories() {
		if (!StringUtils.isBlank(remoteStoryPath)) {
			LOGGER.info("Checking out Stories from " + remoteStoryPath);
			File dstPath = new File(localStoryPath);
			try {
				SVNURL url = SVNURL.parseURIEncoded(remoteStoryPath);
				ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(remoteUser, remotePassword);
				SVNUpdateClient uc = new SVNUpdateClient(authManager, SVNWCUtil.createDefaultOptions(true));
				uc.doCheckout(url, dstPath, SVNRevision.UNDEFINED, SVNRevision.HEAD, SVNDepth.INFINITY, true);
			} catch (SVNException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	@Override
	public void updateStories() {
		if (!StringUtils.isBlank(remoteStoryPath)) {
			File dstPath = new File(localStoryPath);
			try {
				SVNClientManager clientManager = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), remoteUser, remotePassword);
				SVNLogClient logClient = clientManager.getLogClient();
				SVNURL url = SVNURL.parseURIEncoded(remoteStoryPath);
				ISVNDirEntryHandler handler = new ISVNDirEntryHandler() {
	                public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
	                	if(!StringUtils.isBlank(dirEntry.getName())){
	                		LOGGER.info("File found in SVN: " + dirEntry.toString());
	                	}    
	                }
	            };
				logClient.doList(url, SVNRevision.UNDEFINED, SVNRevision.HEAD, true, SVNDepth.INFINITY, SVNDirEntry.DIRENT_ALL, handler);
				LOGGER.info("Updating Stories from " + remoteStoryPath);
				SVNUpdateClient uc = clientManager.getUpdateClient();
				uc.doUpdate(dstPath, SVNRevision.UNDEFINED, SVNDepth.INFINITY, true, true);
			} catch (SVNException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public void setLocalStoryPath(String localStoryPath) {
		this.localStoryPath = localStoryPath;
	}

	public void setRemoteStoryPath(String remoteStoryPath) {
		this.remoteStoryPath = remoteStoryPath;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public void setRemotePassword(String remotePassword) {
		this.remotePassword = remotePassword;
	}
}
