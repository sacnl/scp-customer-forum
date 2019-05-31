package com.sap.cloud.s4hana.examples.addressmgr.blockchain.commands;

import com.google.common.base.Optional;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sap.cloud.s4hana.examples.addressmgr.blockchain.BlacklistService;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.frameworks.hystrix.Command;
import com.sap.cloud.sdk.services.scp.blockchain.hyperledgerfabric.FabricInvocationType;
import com.sap.cloud.sdk.services.scp.blockchain.hyperledgerfabric.FabricService;

import org.slf4j.Logger;

public class AddEmailBlacklistEntryCommand extends Command<Optional<Boolean>> {
    private static final Logger logger = CloudLoggerFactory.getLogger(AddEmailBlacklistEntryCommand.class);

    private final FabricService blacklistService;

    private final String email;

    public AddEmailBlacklistEntryCommand(FabricService blacklistService, final String email) {
        super(HystrixCommandGroupKey.Factory.asKey("LeonardoHyperledgerFabric-blacklistadd"), 5000);
        this.blacklistService = blacklistService;
        this.email = email;
    }

    @Override
    protected Optional<Boolean> run() throws Exception {
        blacklistService.invokeOrQuery(FabricInvocationType.INVOKE, BlacklistService.getBlacklistChaincodeIdOrThrow(), "add", "email",
                getEmail());
        return Optional.of(Boolean.TRUE);
    }

    public String getEmail() {
        return email;
    }

    @Override
    protected Optional<Boolean> getFallback() {
        logger.error("Fallback called when adding email {} to blacklist. Reason:",
                getEmail(), getExecutionException());
        return Optional.absent();
    }

    @Override
    public String toString() {
        return "GetEmailBlacklistStatusCommand{" +
                "email='" + email + '\'' +
                '}';
    }
}
