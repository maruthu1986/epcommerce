/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.policy.common;

import com.elasticpath.cmclient.policy.StatePolicyDelegate;

/**
 * The default delegate class to use. When adding a Delegate, this class should be used, not {@link DefaultStatePolicyGovernableImpl}. 
 */
public class DefaultStatePolicyDelegateImpl extends DefaultStatePolicyGovernableImpl implements StatePolicyDelegate {

}
