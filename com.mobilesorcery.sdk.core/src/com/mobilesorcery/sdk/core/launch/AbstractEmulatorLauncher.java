package com.mobilesorcery.sdk.core.launch;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.mobilesorcery.sdk.core.BuildVariant;
import com.mobilesorcery.sdk.core.CoreMoSyncPlugin;
import com.mobilesorcery.sdk.core.IBuildConfiguration;
import com.mobilesorcery.sdk.core.IBuildResult;
import com.mobilesorcery.sdk.core.IBuildState;
import com.mobilesorcery.sdk.core.IBuildVariant;
import com.mobilesorcery.sdk.core.IPackager;
import com.mobilesorcery.sdk.core.MoSyncProject;
import com.mobilesorcery.sdk.internal.launch.EmulatorLaunchConfigurationDelegate;
import com.mobilesorcery.sdk.profiles.IProfile;

public abstract class AbstractEmulatorLauncher implements IEmulatorLauncher {

	private final String name;

	protected AbstractEmulatorLauncher(String name) {
		this.name = name;
	}

	@Override
	public String getId() {
		throw new UnsupportedOperationException();
	}

	protected File getPackageToInstall(ILaunchConfiguration launchConfig,
			String mode) throws CoreException {
		IProject project = EmulatorLaunchConfigurationDelegate
				.getProject(launchConfig);
		MoSyncProject mosyncProject = MoSyncProject.create(project);
		IBuildVariant variant = EmulatorLaunchConfigurationDelegate.getVariant(
				launchConfig, mode);
		IBuildState buildState = mosyncProject.getBuildState(variant);
		IBuildResult buildResult = buildState.getBuildResult();
		File packageToInstall = buildResult == null ? null : buildResult
				.getBuildResult();
		return packageToInstall;
	}

	protected void assertCorrectPackager(ILaunchConfiguration launchConfig,
			String packagerId, String errormsg) throws CoreException {
		if (!isCorrectPackager(launchConfig, packagerId)) {
			throw new CoreException(new Status(IStatus.ERROR,
					CoreMoSyncPlugin.PLUGIN_ID, errormsg));
		}
	}

	protected boolean isCorrectPackager(ILaunchConfiguration launchConfig, String packagerId) {
		IProject project;
		try {
			project = EmulatorLaunchConfigurationDelegate
					.getProject(launchConfig);
			MoSyncProject mosyncProject = MoSyncProject.create(project);
			IProfile targetProfile = mosyncProject.getTargetProfile();
			IPackager packager = targetProfile.getPackager();
			return packagerId.equals(packager.getId());
		} catch (CoreException e) {
			return false;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * The default behaviour is to make this emulator launcher available in
	 * non-debug modes
	 */
	@Override
	public int isLaunchable(ILaunchConfiguration launchConfiguration, String mode) {
		return EmulatorLaunchConfigurationDelegate.isDebugMode(mode) ? UNLAUNCHABLE : LAUNCHABLE;
	}

	/**
	 * The default behaviour is to return a non-finalizing build with the build
	 * configuration as per specified by the launch configuration and a target
	 * profile set to the currently selected profile.
	 */
	@Override
	public IBuildVariant getVariant(ILaunchConfiguration launchConfig, String mode) throws CoreException {
		IProject project = EmulatorLaunchConfigurationDelegate
				.getProject(launchConfig);
		MoSyncProject mosyncProject = MoSyncProject.create(project);
		IBuildConfiguration cfg = EmulatorLaunchConfigurationDelegate
				.getAutoSwitchBuildConfiguration(launchConfig, mode);
		return new BuildVariant(mosyncProject.getTargetProfile(), cfg, true);
	}

	@Override
	public void setDefaultAttributes(ILaunchConfigurationWorkingCopy wc) {
		// Default impl does nothing.
	}

	@Override
	public String configure(ILaunchConfiguration config, String mode) {
		return getId();
	}

	protected boolean isAutoSelectLaunch(ILaunchConfiguration config, String mode) {
		try {
			IEmulatorLauncher launcher = EmulatorLaunchConfigurationDelegate.getEmulatorLauncher(config, mode);
			return AutomaticEmulatorLauncher.ID.equals(launcher.getId());
		} catch (CoreException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void assertWindows() throws CoreException {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1) {
			throw new CoreException(new Status(IStatus.ERROR,
					CoreMoSyncPlugin.PLUGIN_ID, MessageFormat.format(
							"{0} launches are only supported on Windows",
							getName())));
		}
	}

	protected void assertOSX() throws CoreException {
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1) {
			throw new CoreException(new Status(IStatus.ERROR,
					CoreMoSyncPlugin.PLUGIN_ID, MessageFormat.format(
							"{0} launches are only supported on Mac OS X",
							getName())));
		}
	}
}
