/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.uiespresso.formulaeditor;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import androidx.test.core.app.ApplicationProvider;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class FormulaEditorVariableScopeTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		SettingsFragment.setMultiplayerVariablesPreferenceEnabled(
				ApplicationProvider.getApplicationContext(), true);

		Script script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorAddVariableTestFormulaEditorVariableScopeTest");
		script.addBrick(new ChangeSizeByNBrick(0));

		baseActivityTestRule.launchActivity();

		onView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void variableScopeTest() {
		onDataList()
				.performAdd("GlobalVar", FormulaEditorDataListWrapper.ItemType.VARIABLE, FormulaEditorDataListWrapper.ItemScope.GLOBAL)
				.performAdd("LocalVar", FormulaEditorDataListWrapper.ItemType.VARIABLE, FormulaEditorDataListWrapper.ItemScope.LOCAL)
				.performAdd("LocalVar2", FormulaEditorDataListWrapper.ItemType.VARIABLE, FormulaEditorDataListWrapper.ItemScope.LOCAL)
				.performAdd("MultiplayerVar", FormulaEditorDataListWrapper.ItemType.VARIABLE, FormulaEditorDataListWrapper.ItemScope.MULTIPLAYER);

		onDataList()
				.performAdd("GlobalList", FormulaEditorDataListWrapper.ItemType.LIST, FormulaEditorDataListWrapper.ItemScope.GLOBAL)
				.performAdd("GlobalList2", FormulaEditorDataListWrapper.ItemType.LIST, FormulaEditorDataListWrapper.ItemScope.GLOBAL)
				.performAdd("LocalList", FormulaEditorDataListWrapper.ItemType.LIST, FormulaEditorDataListWrapper.ItemScope.LOCAL);

		onRecyclerView().atPosition(0).onChildView(R.id.headline)
				.check(matches(withText(R.string.multiplayer_vars_headline)));

		onRecyclerView().atPosition(1).onChildView(R.id.headline)
				.check(matches(withText(R.string.global_vars_headline)));

		onRecyclerView().atPosition(2).onChildView(R.id.headline)
				.check(matches(withText(R.string.local_vars_headline)));

		onRecyclerView().perform(scrollToPosition(4));
		onRecyclerView().atPosition(4).onChildView(R.id.headline)
				.check(matches(withText(R.string.global_lists_headline)));

		onRecyclerView().perform(scrollToPosition(6));
		onRecyclerView().atPosition(6).onChildView(R.id.headline)
				.check(matches(withText(R.string.local_lists_headline)));
	}
}
