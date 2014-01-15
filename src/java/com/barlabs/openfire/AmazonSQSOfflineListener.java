package com.barlabs.openfire;

import org.jivesoftware.openfire.OfflineMessageListener;
import org.jivesoftware.openfire.XMPPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class AmazonSQSOfflineListener implements OfflineMessageListener {

	private OfflineAmazonSQSPlugin plugin;

	private static final AmazonSQSOfflineListener instance = new AmazonSQSOfflineListener();

	private static final Logger Log = LoggerFactory.getLogger(AmazonSQSOfflineListener.class);

	private AmazonSQS mSqs;

	public static AmazonSQSOfflineListener getInstance() {
		return instance;
	}

	public AmazonSQSOfflineListener() {
		plugin = (OfflineAmazonSQSPlugin) XMPPServer.getInstance()
					.getPluginManager().getPlugin("offlineamazonsqs");
		mSqs = new AmazonSQSClient(new BasicAWSCredentials(
				plugin.getAWSAccessKey(), plugin.getAWSSecretKey()));
        Region apSouthEast1 = Region.getRegion(Regions.AP_SOUTHEAST_1);
        mSqs.setRegion(apSouthEast1);
	}

	@Override
	public void messageBounced(Message message) {
		sendToSQS(message);
	}

	@Override
	public void messageStored(Message message) {
		sendToSQS(message);
	}

	private void sendToSQS(Message message) {
		try {
			String queueUrl = plugin.getAWSSQSQueueUrl();
			String msg = "{\"to\":\""+ message.getTo() + "\",\"from\":\"" +
							message.getFrom() + "\",\"message\":\"" +
							message.getBody() + "\"}";
			mSqs.sendMessage(new SendMessageRequest(queueUrl, msg));
		} catch (AmazonServiceException ase) {
			Log.error("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            Log.error("Error Message:    " + ase.getMessage(), ase);
            Log.error("HTTP Status Code: " + ase.getStatusCode(), ase);
            Log.error("AWS Error Code:   " + ase.getErrorCode(), ase);
            Log.error("Error Type:       " + ase.getErrorType(), ase);
            Log.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	Log.error("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.", ace);
        	Log.error("Error Message: " + ace.getMessage(), ace);
        }
	}

}

