/*
 * Copyright (c) 2007-2012 bitExpert AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */



package de.bitexpert.eclipse.composer.popup.actions;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import de.bitexpert.eclipse.composer.ComposerActivator;
import de.bitexpert.eclipse.composer.preferences.PreferencePage;


/**
 * The Composer plugin action dealing with the main logic. Subclasses need just
 * to implement the <code>getComposerCommand</code> method to indicate which
 * composer command should be called.
 *
 * @author	Stephan Hochdörfer <S.Hochdoerfer@bitExpert.de>
 */


abstract public class ComposerExecutor implements IObjectActionDelegate
{
	protected IFile selectedFile;


	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
	}


	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{
		if(null != this.selectedFile)
		{
			MessageConsole myConsole       = createConsole("Composer Output");
			final MessageConsoleStream out = myConsole.newMessageStream();
			final IFile selection          = this.selectedFile;

			final String phpExecutable     = getPHPExecutableForPlatform();

			// selectedFile contains the path to the composer.json file the user
			// clicked on. Grab the path and change the file extension to .phar
			final String pharLocation = this.selectedFile.getLocation().toOSString().
				replace(".json", ".phar");
			// the process that we will launch also needs the directory to work
			// in. Thus we chop of the filename from the selected file and get
			// the parent directory the file is located in.
			final String workingDir = this.selectedFile.getLocation().removeLastSegments(1).
				toOSString();

			Job job = new Job("Composer Background Job") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try
					{
						// run composer install task in background
						List<String> commands = new ArrayList<String>();
						commands.add(phpExecutable);
						commands.add(pharLocation);
						commands.add("--no-ansi");
						commands.add("--no-interaction");
						commands.add("install");

						ProcessBuilder pb = new ProcessBuilder(commands);
						pb.directory(new File(workingDir));
						pb.redirectErrorStream(true);
						Process process = pb.start();

						// Read output and write it to the open Eclipse console
						BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String line = null;
						while((line = br.readLine()) != null)
						{
							out.println(line);
						}
						process.waitFor();

						// refresh workspace project to see the newly created files
						selection.getProject().refreshLocal(
							IResource.DEPTH_INFINITE,
							null
						);
					}
					catch(IOException e)
					{
					}
					catch(InterruptedException e)
					{
					}
					catch(CoreException e)
					{
					}

					return Status.OK_STATUS;
				}
			};

			// Start the Job
			job.schedule();
		}
	}


	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection strucSelect = (IStructuredSelection) selection;
			this.selectedFile = (IFile) strucSelect.getFirstElement();
		}
	}


	/**
	 * Creates a console for the given name.
	 *
	 * @param name
	 * @return MessageConsole
	 */
	private MessageConsole createConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager manager = plugin.getConsoleManager();
		MessageConsole myConsole = new MessageConsole(name, null);
		manager.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}


	/**
	 * Returns the path to the PHP executable. Will read the configuration from
	 * the preferences page and if not set fall back to platform dependant default
	 * value.
	 *
	 * @return String
	 */
	private String getPHPExecutableForPlatform()
	{
		// try to try configuration from preferences page
		IPreferenceStore store = ComposerActivator.getDefault().getPreferenceStore();
		String phpExecutable   = store.getString(PreferencePage.PHP_EXECUTABLE);
		// if value is not set, fall back to default value
		if(phpExecutable.isEmpty())
		{
			phpExecutable = (runningOnWindows() ? "php.exe" : "php");
		}

		return phpExecutable;
	}


	/**
	 * Checks if the Operating System is Windows.
	 *
	 * @return bool
	 */
	private boolean runningOnWindows()
	{
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0);
	}


	/**
	 * Returns the name of the composer command to execute.
	 *
	 * @return String
	 */
	abstract protected String getComposerCommand();
}