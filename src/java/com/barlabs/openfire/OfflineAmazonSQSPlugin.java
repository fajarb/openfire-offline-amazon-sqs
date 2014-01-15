package com.barlabs.openfire;

import java.io.File;
import java.util.Map;

import org.jivesoftware.openfire.OfflineMessageStrategy;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;

public class OfflineAmazonSQSPlugin implements Plugin, PropertyEventListener {

	private String awsAccessKey;
	private String awsSecretKey;
	private boolean enabled;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {

        awsAccessKey = JiveGlobals.getProperty("plugin.offline_sqs.aws_access_key", "");
        awsSecretKey = JiveGlobals.getProperty("plugin.offline_sqs.aws_secret_key", "");

        // See if the service is enabled or not.
        enabled = JiveGlobals.getBooleanProperty("plugin.offline_sqs.enabled", false);

        // Listen to system property events
        PropertyEventDispatcher.addListener(this);

        // Listen to offline message strategy
        OfflineMessageStrategy.addListener(AmazonSQSOfflineListener.getInstance());
    }

	@Override
	public void destroyPlugin() {
		OfflineMessageStrategy.removeListener(AmazonSQSOfflineListener.getInstance());
	}

	/**
     * Returns the AWS access key.
     *
     * @return the AWS secret key.
     */
    public String getAWSAccessKey() {
        return awsAccessKey;
    }

    /**
     * Sets the AWS secret key.
     *
     * @param secret the AWS secret key.
     */
    public void setAWSAccessKey(String key) {
        JiveGlobals.setProperty("plugin.offline_sqs.aws_access_key", key);
        this.awsAccessKey = key;
    }

    /**
     * Returns the AWS secret key.
     *
     * @return the AWS secret key.
     */
    public String getAWSSecretKey() {
        return awsSecretKey;
    }

    /**
     * Sets the AWS access key.
     *
     * @param secret the AWS secret key.
     */
    public void setAWSSecretKey(String secret) {
        JiveGlobals.setProperty("plugin.offline_sqs.aws_secret_key", secret);
        this.awsSecretKey = secret;
    }

    /**
     * Returns true if the user service is enabled. If not enabled, it will not accept
     * requests to create new accounts.
     *
     * @return true if the user service is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables the user service. If not enabled, it will not accept
     * requests to create new accounts.
     *
     * @param enabled true if the user service should be enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        JiveGlobals.setProperty("plugin.offline_sqs.enabled",  enabled ? "true" : "false");
    }

	@Override
	public void propertySet(String property, Map<String, Object> params) {
		if (property.equals("plugin.offline_sqs.aws_access_key")) {
            this.awsAccessKey = "";
        }
        else if (property.equals("plugin.offline_sqs.aws_secret_key")) {
            this.awsSecretKey = "";
        }
	}

	@Override
	public void propertyDeleted(String property, Map<String, Object> params) {
		// Do nothing
	}

	@Override
	public void xmlPropertySet(String property, Map<String, Object> params) {
		// Do nothing
	}

	@Override
	public void xmlPropertyDeleted(String property, Map<String, Object> params) {
		// Do nothing
	}
}