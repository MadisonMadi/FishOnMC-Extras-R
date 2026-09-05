package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.annotations.RootConfig;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 3)
@RootConfig
@Translatable.Name("FishOnExtras Rebirth")
@IgnoreVisibility
public class MainConfig extends Config {
    public MainConfig() {
        super(Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "main_config"));
    }

    @Name("§7§oDev§8§o: §f§oEnable Mod")
    @Desc("§4WARNING\nThis will turn off the mod when false. NOTHING will work when false.\nTo turn it back on, do /foe config")
    public ValidatedBoolean enableMod = new ValidatedBoolean(true);

    @Name("Wiki URL")
    @Desc("§7The URL used for the wiki button")
    public ValidatedString wikiPageUrl = new ValidatedString.Builder("https://github.com/FishOnExtras/FishonMC-Extras-R/wiki/Placeholders-0.3.8").withCorrector().build();

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON;
    }
}
