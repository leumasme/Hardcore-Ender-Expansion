package chylex.hee.world.structure.island.biome.feature.mountains;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.ComponentScatteredFeatureIsland;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureMiningSpot extends AbstractIslandStructure{
	private byte iterationsLeft;
	
	@Override
	protected boolean generate(Random rand){
		int x = getRandomXZ(rand,32), z = getRandomXZ(rand,32), y = 5+rand.nextInt(15+rand.nextInt(30));
		if (world.getBlock(x,y,z) != Blocks.end_stone)return false;
		
		double rad = 1.5D+rand.nextDouble()*0.5D;
		iterationsLeft = (byte)(60+rand.nextInt(50));
		
		generateBlob(rand,x,y,z,rad,0);
		return true;
	}
	
	private void generateBlob(Random rand, int x, int y, int z, double rad, int recursionLevel){
		if (x <= rad || z <= rad || x >= ComponentScatteredFeatureIsland.size-rad || z >= ComponentScatteredFeatureIsland.size-rad)return;
		if (--iterationsLeft == 0 || (recursionLevel > 0 && rand.nextInt(30-recursionLevel*2) == 0) || recursionLevel > 12)return;
		
		int xx, yy, zz;
		double dist;
		
		for(xx = (int)Math.floor(x-rad)-1; xx <= x+rad+1; xx++){
			for(yy = (int)Math.floor(y-rad)-1; yy <= y+rad+1; yy++){
				if (yy <= 0)continue;
				
				for(zz = (int)Math.floor(z-rad)-1; zz <= z+rad+1; zz++){
					dist = MathUtil.distance(xx-x,yy-y,zz-z);
					
					if (world.getBlock(xx,yy,zz) == Blocks.end_stone && rand.nextInt(4) == 0 && dist <= rad-rand.nextDouble()*0.5D){
						placeBlock(rand,xx,yy,zz,dist/rad);
					}
				}
			}
		}
		
		if (rand.nextInt(20) == 0)generateBlob(rand,x+rand.nextInt(3)-rand.nextInt(3),y+rand.nextInt(3)-rand.nextInt(3),z+rand.nextInt(3)-rand.nextInt(3),rad,recursionLevel+1);
		generateBlob(rand,x+rand.nextInt(3)-rand.nextInt(3),y+rand.nextInt(3)-rand.nextInt(3),z+rand.nextInt(3)-rand.nextInt(3),rad,recursionLevel);
	}
	
	private void placeBlock(Random rand, int x, int y, int z, double distPercent){
		if (rand.nextBoolean() && rand.nextBoolean() && rand.nextDouble() > distPercent-0.2D-rand.nextDouble()*0.25D){
			int type = rand.nextInt(100);
			
			if (type < 8)world.setBlock(x,y,z,Blocks.emerald_ore);
			else if (type < 16)world.setBlock(x,y,z,Blocks.lapis_ore);
			else if (type < 30)world.setBlock(x,y,z,Blocks.diamond_ore);
			else if (type < 55)world.setBlock(x,y,z,Blocks.gold_ore);
			else if (type < 80)world.setBlock(x,y,z,Blocks.iron_ore);
			else world.setBlock(x,y,z,Blocks.coal_ore);
		}
		else if (rand.nextBoolean())world.setBlock(x,y,z,Blocks.stone);
	}
}
