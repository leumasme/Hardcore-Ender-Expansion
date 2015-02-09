package chylex.hee.gui;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.util.MathUtil;

@SideOnly(Side.CLIENT)
public class GuiAchievementsCustom extends GuiAchievements{
	private GuiButton nextPageButton;
	private AchievementPage achievements;
	
	public GuiAchievementsCustom(GuiScreen parent, StatFileWriter statWriter){
		super(parent,new ShowAllReadStatFile(statWriter));
	}
	
	@Override
	public void initGui(){
		super.initGui();
		
		for(Iterator<GuiButton> iter = buttonList.iterator(); iter.hasNext();){
			GuiButton btn = iter.next();
			
			if (btn.id == 1){
				btn.width = 120;
				btn.xPosition = (width>>1)-(btn.width>>1);
			}
			else if (btn.id == 2){
				nextPageButton = btn;
				iter.remove();
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int buttonId) throws IOException{
		if (buttonId == 0 && achievements != null){
			int offsetX = MathUtil.clamp(MathUtil.floor(field_146567_u),AchievementList.minDisplayColumn*24-112,AchievementList.maxDisplayColumn*24-78); // OBFUSCATED viewportX
			int offsetY = MathUtil.clamp(MathUtil.floor(field_146566_v),AchievementList.minDisplayRow*24-112,AchievementList.maxDisplayRow*24-78); // OBFUSCATED viewportY
			
			int centerX = (width-field_146555_f)/2+16; // OBFUSCATED viewportWidth, 256
			int centerY = (height-field_146557_g)/2+17; // OBFUSCATED viewportHeight, 202

			float realMouseX = (mouseX-centerX)*field_146570_r; // OBFUSCATED viewportScale
			float realMouseY = (mouseY-centerY)*field_146570_r;
			
			for(Achievement achievement:achievements.getAchievements()){
				int x = achievement.displayColumn*24-offsetX;
				int y = achievement.displayRow*24-offsetY;

				if (x >= -24 && y >= -24 && x <= 224F*field_146570_r && y <= 155F*field_146570_r && realMouseX >= x && realMouseX <= x+22 && realMouseY >= y && realMouseY <= y+22){
					KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj = KnowledgeUtils.tryGetFromItemStack(achievement.theItemStack);
					if (obj != null)CompendiumEventsClient.openCompendium(obj);
				}
			}
		}
		
		super.mouseClicked(mouseX,mouseY,buttonId);
	}
	
	@Override
	public void doneLoading(){
		super.doneLoading();
		
		for(int a = 0; a < AchievementPage.getAchievementPages().size(); a++){
			try{
				actionPerformed(nextPageButton);
			}catch(IOException e){}
			
			if (nextPageButton.displayString.equals(AchievementManager.achievementScreenName)){
				achievements = AchievementPage.getAchievementPage(AchievementManager.achievementScreenName);
				break;
			}
		}
	}
	
	static final class ShowAllReadStatFile extends StatFileWriter{
		private final StatFileWriter wrapped;
		
		public ShowAllReadStatFile(StatFileWriter wrapped){
			this.wrapped = wrapped;
		}
		
		@Override
		public boolean canUnlockAchievement(Achievement achievement){
			return wrapped.canUnlockAchievement(achievement);
		}
		
		@Override
		public boolean hasAchievementUnlocked(Achievement achievement){
			return wrapped.hasAchievementUnlocked(achievement);
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public int func_150874_c(Achievement achievement){ // OBFUSCATED getUnlockDepth
			return hasAchievementUnlocked(achievement) ? 0 : 1;
		}
	}
}
