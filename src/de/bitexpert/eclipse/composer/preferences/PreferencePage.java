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


package de.bitexpert.eclipse.composer.preferences;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import de.bitexpert.eclipse.composer.ComposerActivator;


/**
 * Preferences Page for the Composer Plugin to be able to define the location
 * of the PHP executable.
 *
 * @author	Stephan Hochdörfer <S.Hochdoerfer@bitExpert.de>
 */


public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	final public static String PHP_EXECUTABLE = "PHP";

	/**
	 * Creates a new {@link PreferencePage}.
	 */
	public PreferencePage()
	{
		super(GRID);
		setPreferenceStore(ComposerActivator.getDefault().getPreferenceStore());
		setDescription("Composer Configuration");
	}


	@Override
	public void init(IWorkbench arg0)
	{
	}


	@Override
	protected void createFieldEditors()
	{
		// create field for preferences page
		this.addField(
			new StringFieldEditor(
				PHP_EXECUTABLE,
				"Path to PHP Executable",
				getFieldEditorParent()
			)
		);
	}
}