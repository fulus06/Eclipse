/*  Copyright (C) 2011 Mobile Sorcery AB

    This program is free software; you can redistribute it and/or modify it
    under the terms of the Eclipse Public License v1.0.

    This program is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE. See the Eclipse Public License v1.0 for
    more details.

    You should have received a copy of the Eclipse Public License v1.0 along
    with this program. It is also available at http://www.eclipse.org/legal/epl-v10.html
*/
package com.mobilesorcery.sdk.core.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.mobilesorcery.sdk.core.IBuildVariant;
import com.mobilesorcery.sdk.core.MoSyncProject;

/**
 * <p>An interface for launching mobile apps on different platforms (such as the MoRe
 * emulator, the Android emulator, etc).</p>
 * <p>Currently, only emulators are supported due to the fact that there is already
 * a mechanism to send apps to physical devices via the toolbar.</p>
 * @author Mattias Bybro, mattias.bybro@purplescout.se; mattias@bybro.com
 *
 */
public interface IEmulatorLauncher {

	/**
	 * The extension points to hook into for clients implementing this interface.
	 */
    public static final String EXTENSION_POINT_ID = "com.mobilesorcery.core.launcher";

    public static final int LAUNCHABLE = 1 << 1;

    public static final int UNLAUNCHABLE = 1 << 2;

    public static final int REQUIRES_CONFIGURATION = 1 << 3;

	public void launch(ILaunchConfiguration launchConfig, String mode, ILaunch launch, int emulatorId, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns the id of this launcher.
	 */
	public String getId();

	/**
	 * Returns the user-friendly name of this launcher.
	 */
	public String getName();

	/**
	 * Returns {@code true} if this launcher is available for a certain launch configuration.
	 * Clients need to consider the device profile associated with the launch configuration.
	 * @return One of the values {@link IEmulatorLauncher#LAUNCHABLE}, {@link IEmulatorLauncher#UNLAUNCHABLE},
	 * {@link IEmulatorLauncher#REQUIRES_CONFIGURATION}. If it requires configuration,
	 * then it is not launchable.
	 */
	public int isLaunchable(ILaunchConfiguration launchConfig, String mode);

	/**
	 * Returns the variant to use for this launcher.
	 * @param launchConfig
	 * @param mode
	 * @return
	 * @throws CoreException
	 */
	public IBuildVariant getVariant(ILaunchConfiguration launchConfig, String mode) throws CoreException;

	/**
	 * Sets the default launch configuration attributes for this emulator launcher
	 * @param wc
	 */
	public void setDefaultAttributes(ILaunchConfigurationWorkingCopy wc);

	// HACK!
	public String configure(ILaunchConfiguration config, String mode);
}
