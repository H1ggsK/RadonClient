package skid.krypton.module.modules.donut;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import skid.krypton.event.EventListener;
import skid.krypton.event.events.StartTickEvent;
import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.module.setting.Setting;
import skid.krypton.module.setting.*;
import skid.krypton.utils.EncryptedString;

import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class AuctionSniper
        extends Module {
    private final ItemSetting snipingItem = new ItemSetting(EncryptedString.of("Sniping Item"), Items.AIR);
    private final StringSetting price = new StringSetting(EncryptedString.of("Price"), "1k");
    private final ModeSetting<Mode> mode = new ModeSetting(EncryptedString.of("Mode"), Mode.MANUAL, Mode.class).setDescription(EncryptedString.of("Manual is faster but API doesn't require auction gui opened all the time"));
    private final StringSetting apiKey = new StringSetting(EncryptedString.of("Api Key"), "").setDescription(EncryptedString.of("You can get it by typing /api in chat"));
    private final NumberSetting refreshDelay = new NumberSetting(EncryptedString.of("Refresh Delay"), 0.0, 100.0, 2.0, 1.0);
    private final NumberSetting buyDelay = new NumberSetting(EncryptedString.of("Buy Delay"), 0.0, 100.0, 2.0, 1.0);
    private final NumberSetting apiRefreshRate = new NumberSetting(EncryptedString.of("API Refresh Rate"), 10.0, 5000.0, 250.0, 10.0).getValue(EncryptedString.of("How often to query the API (in milliseconds)"));
    private final BooleanSetting showApiNotifications = new BooleanSetting(EncryptedString.of("Show API Notifications"), true).setDescription(EncryptedString.of("Show chat notifications for API actions"));
    private int delayCounter;
    private boolean isProcessing;
    private final HttpClient httpClient;
    private final Gson gson;
    private long lastApiCallTimestamp = 0L;
    private final Map<String, Double> snipingItems = new HashMap<String, Double>();
    private boolean isApiQueryInProgress = false;
    private boolean isAuctionSniping = false;
    private int auctionPageCounter = -1;
    private String currentSellerName = "";

    public AuctionSniper() {
        super(EncryptedString.of("Auction Sniper"), EncryptedString.of("Snipes items on auction house for cheap"), -1, Category.DONUT);
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).build();
        this.gson = new Gson();
        Setting[] settingArray = new Setting[]{this.snipingItem, this.price, this.mode, this.apiKey, this.refreshDelay, this.buyDelay, this.apiRefreshRate, this.showApiNotifications};
        this.addSettings(settingArray);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        double d = this.parsePrice(this.price.getValue());
        if (d == -1.0) {
            if (this.mc.player != null) {
                ClientPlayerEntity clientPlayerEntity = this.mc.player;
                clientPlayerEntity.sendMessage(Text.of("Invalid Price"), true);
            }
            this.toggle();
            return;
        }
        if (this.snipingItem.getItem() != Items.AIR) {
            Map<String, Double> map = this.snipingItems;
            map.put(this.snipingItem.getItem().toString(), d);
        }
        this.lastApiCallTimestamp = 0L;
        this.isApiQueryInProgress = false;
        this.isAuctionSniping = false;
        this.currentSellerName = "";
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.isAuctionSniping = false;
    }

    @EventListener
    public void onTick(StartTickEvent startTickEvent) {
        block10: {
            block9: {
                if (this.mc.player == null) {
                    return;
                }
                if (this.delayCounter > 0) {
                    --this.delayCounter;
                    return;
                }
                if (this.mode.isMode(Mode.API)) {
                    this.handleApiMode();
                    return;
                }
                if (!this.mode.isMode(Mode.MANUAL)) break block9;
                ScreenHandler screenHandler = this.mc.player.currentScreenHandler;
                if (!(this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)) break block10;
                if (((GenericContainerScreenHandler)screenHandler).getRows() == 6) {
                    this.processSixRowAuction((GenericContainerScreenHandler)screenHandler);
                } else if (((GenericContainerScreenHandler)screenHandler).getRows() == 3) {
                    this.processThreeRowAuction((GenericContainerScreenHandler)screenHandler);
                }
            }
            return;
        }
        String[] stringArray = this.snipingItem.getItem().getTranslationKey().split("\\.");
        String string2 = stringArray[stringArray.length - 1];
        String string3 = Arrays.stream(string2.replace("_", " ").split(" ")).map(string -> string.substring(0, 1).toUpperCase() + string.substring(1)).collect(Collectors.joining(" "));
        this.mc.getNetworkHandler().sendChatCommand("ah " + string3);
        this.delayCounter = 20;
    }

    private void handleApiMode() {
        block10: {
            block12: {
                block8: {
                    block11: {
                        block9: {
                            if (!this.isAuctionSniping) break block8;
                            ScreenHandler screenHandler = this.mc.player.currentScreenHandler;
                            if (!(this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)) break block9;
                            this.auctionPageCounter = -1;
                            if (((GenericContainerScreenHandler)screenHandler).getRows() == 6) {
                                this.processSixRowAuction((GenericContainerScreenHandler)screenHandler);
                            } else if (((GenericContainerScreenHandler)screenHandler).getRows() == 3) {
                                this.processThreeRowAuction((GenericContainerScreenHandler)screenHandler);
                            }
                            break block10;
                        }
                        if (this.auctionPageCounter != -1) break block11;
                        this.mc.getNetworkHandler().sendChatCommand("ah " + this.currentSellerName);
                        this.auctionPageCounter = 0;
                        break block10;
                    }
                    if (this.auctionPageCounter <= 40) break block12;
                    this.isAuctionSniping = false;
                    this.currentSellerName = "";
                    break block10;
                }
                if (this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler && this.mc.currentScreen.getTitle().getString().contains("Page")) {
                    this.mc.player.closeHandledScreen();
                    this.delayCounter = 20;
                    return;
                }
                if (this.isApiQueryInProgress) {
                    return;
                }
                long l = System.currentTimeMillis();
                long l2 = l - this.lastApiCallTimestamp;
                if (l2 > (long)this.apiRefreshRate.getIntValue()) {
                    this.lastApiCallTimestamp = l;
                    if (this.apiKey.getValue().isEmpty()) {
                        if (this.showApiNotifications.getValue()) {
                            ClientPlayerEntity clientPlayerEntity = this.mc.player;
                            clientPlayerEntity.sendMessage(Text.of("\u00a7cAPI key is not set. Set it using /api in-game."), false);
                        }
                        return;
                    }
                    this.isApiQueryInProgress = true;
                    this.queryApi().thenAccept(this::processApiResponse);
                }
                return;
            }
            ++this.auctionPageCounter;
        }
    }

    private CompletableFuture<List<?>> queryApi() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String string = "https://api.donutsmp.net/v1/auction/list/" + 1;
                HttpResponse<String> httpResponse = this.httpClient.send(HttpRequest.newBuilder().uri(URI.create(string)).header("Authorization", "Bearer " + this.apiKey.getValue()).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString("{\"sort\": \"recently_listed\"}")).build(), HttpResponse.BodyHandlers.ofString());
                if (httpResponse.statusCode() != 200) {
                    if (this.showApiNotifications.getValue() && this.mc.player != null) {
                        ClientPlayerEntity clientPlayerEntity = this.mc.player;
                        clientPlayerEntity.sendMessage(Text.of("\u00a7cAPI Error: " + httpResponse.statusCode()), false);
                    }
                    ArrayList<?> arrayList = new ArrayList<>();
                    this.isApiQueryInProgress = false;
                    return arrayList;
                }
                Gson gson = this.gson;
                JsonArray jsonArray = gson.fromJson(httpResponse.body(), JsonObject.class).getAsJsonArray("result");
                ArrayList<JsonObject> arrayList = new ArrayList<>();
                for (JsonElement jsonElement : jsonArray) {
                    arrayList.add(jsonElement.getAsJsonObject());
                }
                this.isApiQueryInProgress = false;
                return arrayList;
            } catch (Throwable _t) {
                _t.printStackTrace(System.err);
                return List.of();
            }
        });
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void processApiResponse(List list) {
        block2: for (Object e : list) {
            try {
                double d;
                String string;
                String string2 = ((JsonObject)e).getAsJsonObject("item").get("id").getAsString();
                long l = ((JsonObject)e).get("price").getAsLong();
                String string3 = ((JsonObject)e).getAsJsonObject("seller").get("name").getAsString();
                Iterator<Map.Entry<String, Double>> iterator = this.snipingItems.entrySet().iterator();
                do {
                    if (!iterator.hasNext()) continue block2;
                    Map.Entry<String, Double> entry = iterator.next();
                    string = entry.getKey();
                    d = entry.getValue();
                } while (!string2.contains(string) || !((double)l <= d));
                if (this.showApiNotifications.getValue() && this.mc.player != null) {
                    ClientPlayerEntity clientPlayerEntity = this.mc.player;
                    clientPlayerEntity.sendMessage(Text.of("\u00a7aFound " + string2 + " for " + this.formatPrice(l) + " \u00a7r(threshold: " + this.formatPrice(d) + ") \u00a7afrom seller: " + string3), false);
                }
                this.isAuctionSniping = true;
                this.currentSellerName = string3;
                return;
            }
            catch (Exception exception) {
                if (!this.showApiNotifications.getValue() || this.mc.player == null) continue;
                ClientPlayerEntity clientPlayerEntity = this.mc.player;
                clientPlayerEntity.sendMessage(Text.of("\u00a7cError processing auction: " + exception.getMessage()), false);
            }
        }
    }

    private void processSixRowAuction(GenericContainerScreenHandler genericContainerScreenHandler) {
        ItemStack itemStack = genericContainerScreenHandler.getSlot(47).getStack();
        if (itemStack.isOf(Items.AIR)) {
            this.delayCounter = 2;
            return;
        }
        for (Object e : itemStack.getTooltip(Item.TooltipContext.DEFAULT, this.mc.player, TooltipType.BASIC)) {
            String string = e.toString();
            if (!string.contains("Recently Listed") || !((Text)e).getStyle().toString().contains("white") && !string.contains("white")) continue;
            this.mc.interactionManager.clickSlot(this.mc.player.currentScreenHandler.syncId, 47, 1, SlotActionType.QUICK_MOVE, this.mc.player);
            this.delayCounter = 5;
            return;
        }
        for (int i = 0; i < 44; ++i) {
            ItemStack itemStack2 = genericContainerScreenHandler.getSlot(i).getStack();
            if (!itemStack2.isOf(this.snipingItem.getItem()) || !this.isValidAuctionItem(itemStack2)) continue;
            if (this.isProcessing) {
                this.mc.interactionManager.clickSlot(this.mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.QUICK_MOVE, this.mc.player);
                this.isProcessing = false;
                return;
            }
            this.isProcessing = true;
            this.delayCounter = this.buyDelay.getIntValue();
            return;
        }
        if (this.isAuctionSniping) {
            this.isAuctionSniping = false;
            this.currentSellerName = "";
            this.mc.player.closeHandledScreen();
        } else {
            this.mc.interactionManager.clickSlot(this.mc.player.currentScreenHandler.syncId, 49, 1, SlotActionType.QUICK_MOVE, this.mc.player);
            this.delayCounter = this.refreshDelay.getIntValue();
        }
    }

    private void processThreeRowAuction(GenericContainerScreenHandler genericContainerScreenHandler) {
        if (this.isValidAuctionItem(genericContainerScreenHandler.getSlot(13).getStack())) {
            this.mc.interactionManager.clickSlot(this.mc.player.currentScreenHandler.syncId, 15, 1, SlotActionType.QUICK_MOVE, this.mc.player);
            this.delayCounter = 20;
        }
        if (this.isAuctionSniping) {
            this.isAuctionSniping = false;
            this.currentSellerName = "";
        }
    }

    private double parseTooltipPrice(List list) {
        String string;
        String string2;
        block2: {
            if (list == null || list.isEmpty()) {
                return -1.0;
            }
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                String string3 = ((Text)iterator.next()).getString();
                if (!string3.matches("(?i).*price\\s*:\\s*\\$.*")) continue;
                String string4 = string3.replaceAll("[,$]", "");
                Matcher matcher = Pattern.compile("([\\d]+(?:\\.[\\d]+)?)\\s*([KMB])?", 2).matcher(string4);
                if (!matcher.find()) continue;
                string2 = matcher.group(1);
                string = matcher.group(2) != null ? matcher.group(2).toUpperCase() : "";
                break block2;
            }
            return -1.0;
        }
        return this.parsePrice(string2 + string);
    }

    private boolean isValidAuctionItem(ItemStack itemStack) {
        List list = itemStack.getTooltip(Item.TooltipContext.DEFAULT, this.mc.player, TooltipType.BASIC);
        double d = this.parseTooltipPrice(list) / (double)itemStack.getCount();
        double d2 = this.parsePrice(this.price.getValue());
        if (d2 == -1.0) {
            if (this.mc.player != null) {
                ClientPlayerEntity clientPlayerEntity = this.mc.player;
                clientPlayerEntity.sendMessage(Text.of("Invalid Price"), true);
            }
            this.toggle();
            return false;
        }
        if (d == -1.0) {
            if (this.mc.player != null) {
                ClientPlayerEntity clientPlayerEntity = this.mc.player;
                clientPlayerEntity.sendMessage(Text.of("Invalid Auction Item Price"), true);
                for (int i = 0; i < list.size() - 1; ++i) {
                    PrintStream printStream = System.out;
                    printStream.println(i + ". " + ((Text)list.get(i)).getString());
                }
            }
            this.toggle();
            return false;
        }
        boolean bl = d <= d2;
        return bl;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private double parsePrice(String string) {
        if (string == null) return -1.0;
        if (string.isEmpty()) {
            return -1.0;
        }
        String string2 = string.trim().toUpperCase();
        double d = 1.0;
        if (string2.endsWith("B")) {
            d = 1.0E9;
            string2 = string2.substring(0, string2.length() - 1);
        } else if (string2.endsWith("M")) {
            d = 1000000.0;
            string2 = string2.substring(0, string2.length() - 1);
        } else if (string2.endsWith("K")) {
            d = 1000.0;
            string2 = string2.substring(0, string2.length() - 1);
        }
        try {
            return Double.parseDouble(string2) * d;
        }
        catch (NumberFormatException numberFormatException) {
            return -1.0;
        }
    }

    private String formatPrice(double d) {
        if (d >= 1.0E9) {
            Object[] objectArray = new Object[]{d / 1.0E9};
            return String.format("%.2fB", objectArray);
        }
        if (d >= 1000000.0) {
            Object[] objectArray = new Object[]{d / 1000000.0};
            return String.format("%.2fM", objectArray);
        }
        if (d >= 1000.0) {
            Object[] objectArray = new Object[]{d / 1000.0};
            return String.format("%.2fK", objectArray);
        }
        Object[] objectArray = new Object[]{d};
        return String.format("%.2f", objectArray);
    }

    public enum Mode {
        API("API", 0),
        MANUAL("MANUAL", 1);

        Mode(final String name, final int ordinal) {
        }
    }

}
