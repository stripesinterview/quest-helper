package com.questhelper.helpers.quests.thecurseofarrav;

import com.questhelper.MockedTest;
import com.questhelper.domain.AccountType;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.statemanagement.AchievementDiaryStepManager;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.tools.QuestPerspective;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Node;
import net.runelite.api.Player;
import net.runelite.api.Scene;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class KeysAndLeversTest extends MockedTest
{
	private MockedStatic<QuestPerspective> questPerspectiveMockedStatic;
	private MockedStatic<WorldPoint> worldPointMockedStatic;
	private TheCurseOfArrav helper;

	@BeforeEach
	protected void lol()
	{
		when(playerStateManager.getAccountType()).thenReturn(AccountType.NORMAL);

		var mockedPlayer = Mockito.mock(Player.class);
		// when(mockedPlayer.getLocalLocation()).thenReturn(new LocalPoint(1, 1, 1));
		when(client.getLocalPlayer()).thenReturn(mockedPlayer);

		questPerspectiveMockedStatic = Mockito.mockStatic(QuestPerspective.class);

		worldPointMockedStatic = Mockito.mockStatic(WorldPoint.class);

		questPerspectiveMockedStatic.when(() -> QuestPerspective.getInstanceLocalPointFromReal(any(), any()))
			.thenReturn(null);

		helper = new TheCurseOfArrav();

	}

	@AfterEach
	protected void lel()
	{
		questPerspectiveMockedStatic.close();
		worldPointMockedStatic.close();
	}

	private ConditionalStep init(WorldPoint playerLocation)
	{
		return this.init(playerLocation, null);
	}

	private ConditionalStep init(WorldPoint playerLocation, Item[] mockedItems)
	{
		worldPointMockedStatic.when(() -> WorldPoint.fromLocalInstance(any(), any()))
			.thenReturn(playerLocation);

		var mockedItemContainer = Mockito.mock(ItemContainer.class);
		if (mockedItems != null) {
			when(mockedItemContainer.getItems()).thenReturn(mockedItems);
			when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(mockedItemContainer);
		}

		when(client.getPlane()).thenReturn(0);

		var mockedScene = Mockito.mock(Scene.class);
		when(mockedScene.getTiles()).thenReturn(new Tile[][][]{
			{}
		});
		when(client.getScene()).thenReturn(mockedScene);

		this.injector.injectMembers(helper);
		helper.setInjector(injector);
		helper.setQuest(QuestHelperQuest.THE_CURSE_OF_ARRAV);
		helper.setQuestHelperPlugin(questHelperPlugin);
		helper.setConfig(questHelperConfig);
		helper.init();

		helper.startUp(questHelperConfig);
		var conditionalStep = helper.unlockImposingDoors;
		conditionalStep.startUp();
		return conditionalStep;
	}

	@Test
	void ensureOutsideTomb()
	{
		var conditionalStep = this.init(new WorldPoint(3305, 3037, 0));

		assertEquals(this.helper.enterTomb, conditionalStep.getActiveStep());
	}

	@Test
	void getFirstKey()
	{
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0));

		assertEquals(this.helper.getFirstKey, conditionalStep.getActiveStep());
	}

	@Test
	void getSecondKey()
	{
		var mockedItems = new Item[]{new Item(ItemID.MASTABA_KEY, 1)};
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getSecondKey, conditionalStep.getActiveStep());
	}

	@Test
	void getToSouthLever()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY, 1),
			new Item(ItemID.MASTABA_KEY_30309, 1),
		};
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void insertKeyIntoSouthLever()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY, 1),
			new Item(ItemID.MASTABA_KEY_30309, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(0);
		var conditionalStep = this.init(new WorldPoint(3893, 4552, 0), mockedItems);

		assertEquals(this.helper.pullSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void getToSouthLeverAfterInsertingKey1()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void getToSouthLeverAfterInsertingKey2()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY_30309, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void pullSouthLeverAfterInsertingKey1()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3893, 4552, 0), mockedItems);

		assertEquals(this.helper.pullSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void pullSouthLeverAfterInsertingKey2()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY_30309, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3893, 4552, 0), mockedItems);

		assertEquals(this.helper.pullSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void leaveSouthLeverAfterInsertingKey1()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(2);
		var conditionalStep = this.init(new WorldPoint(3893, 4552, 0), mockedItems);

		assertEquals(this.helper.leaveSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void leaveSouthLeverAfterInsertingKey2()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY_30309, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(2);
		var conditionalStep = this.init(new WorldPoint(3893, 4552, 0), mockedItems);

		assertEquals(this.helper.leaveSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void goToNorthLeverAfterPullingSouthLeverKey1()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(2);
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToNorthLever, conditionalStep.getActiveStep());
	}

	@Test
	void goToNorthLeverAfterPullingSouthLeverKey2()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY_30309, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(2);
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToNorthLever, conditionalStep.getActiveStep());
	}

	@Test
	void insertKeyIntoNorthLeverAfterPullingSouthLeverKey1()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(2);
		var conditionalStep = this.init(new WorldPoint(3894, 4597, 0), mockedItems);

		assertEquals(this.helper.pullNorthLever, conditionalStep.getActiveStep());
	}

	@Test
	void insertKeyIntoNorthLeverAfterPullingSouthLeverKey2()
	{
		var mockedItems = new Item[]{
			new Item(ItemID.MASTABA_KEY_30309, 1),
		};
		when(client.getVarbitValue(11482)).thenReturn(2);
		var conditionalStep = this.init(new WorldPoint(3894, 4597, 0), mockedItems);

		assertEquals(this.helper.pullNorthLever, conditionalStep.getActiveStep());
	}

	@Test
	void getToSouthLeverAfterInsertingKey()
	{
		var mockedItems = new Item[]{
		};
		when(client.getVarbitValue(11482)).thenReturn(2);
		when(client.getVarbitValue(11481)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToNorthLever, conditionalStep.getActiveStep());
	}

	@Test
	void pullNorthLeverAfterPullingSouthLeverKey1()
	{
		var mockedItems = new Item[]{
		};
		when(client.getVarbitValue(11482)).thenReturn(2);
		when(client.getVarbitValue(11481)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3894, 4597, 0), mockedItems);

		assertEquals(this.helper.pullNorthLever, conditionalStep.getActiveStep());
	}

	@Test
	void pullNorthLeverAfterPullingSouthLeverKey2()
	{
		var mockedItems = new Item[]{
		};
		when(client.getVarbitValue(11482)).thenReturn(2);
		when(client.getVarbitValue(11481)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3894, 4597, 0), mockedItems);

		assertEquals(this.helper.pullNorthLever, conditionalStep.getActiveStep());
	}
}
