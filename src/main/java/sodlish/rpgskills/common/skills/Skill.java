package sodlish.rpgskills.common.skills;

public enum Skill
{
    MINING (0, "skill.mining"),
    COOKING (1, "skill.cooking"),
    POWER (2, "skill.power"),
    DEFENCE(3, "skill.defence"),
    CRAFTING (4, "skill.crafting"),
    FARMING (5, "skill.farming"),
    FOCUS (6, "skill.focus"),
    MAGIC (7, "skill.magic");
    
    public final int index;
    public final String displayName;
    
    Skill(int index, String name)
    {
        this.index = index;
        this.displayName = name;
    }
}