package com.h1ggsk.radon.utils.embed;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

public class DiscordWebhook {
    private final String content;
    private String b;
    private String c;
    private String d;
    private boolean e;
    private final List<EmbedObject> embeds;

    public DiscordWebhook(final String a) {
        this.embeds = new ArrayList<>();
        this.content = a;
    }

    public void a(final String b) {
        this.b = b;
    }

    public void b(final String c) {
        this.c = c;
    }

    public void c(final String d) {
        this.d = d;
    }

    public void a(final boolean e) {
        this.e = e;
    }

    public void addEmbed(final EmbedObject bn) {
        this.embeds.add(bn);
    }

    @SuppressWarnings("deprecation")
    public void execute() throws Throwable {
        if (this.b == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }
        final JSONObject JSONSerializer = new JSONObject();
        JSONSerializer.put("content", this.b);
        JSONSerializer.put("username", this.c);
        JSONSerializer.put("avatar_url", this.d);
        JSONSerializer.put("tts", this.e);
        if (!this.embeds.isEmpty()) {
            final ArrayList<JSONObject> list = new ArrayList<>();
            for (EmbedObject next : this.embeds) {
                JSONObject jsonEmbed = new JSONObject();
                jsonEmbed.put("title", next.title);
                jsonEmbed.put("description", next.description);
                jsonEmbed.put("url", next.url);
                if (next.color != null) {
                    Color color = next.color;
                    jsonEmbed.put("color", ((color.getRed() << 8) + color.getGreen() << 8) + color.getBlue());
                }
                Footer footer = next.footer;
                Image image = next.image;
                Thumbnail thumbnail = next.thumbnail;
                Author author = next.author;
                List<Field> fields = next.fields;
                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();
                    jsonFooter.put("text", footer.text);
                    jsonFooter.put("icon_url", footer.iconUrl);
                    jsonEmbed.put("footer", jsonFooter);
                }
                if (image != null) {
                    JSONObject jsonImage = new JSONObject();
                    jsonImage.put("url", image.url);
                    jsonEmbed.put("image", jsonImage);
                }
                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();
                    jsonThumbnail.put("url", thumbnail.url);
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }
                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();
                    jsonAuthor.put("name", author.name);
                    jsonAuthor.put("url", author.url);
                    jsonAuthor.put("icon_url", author.iconUrl);
                    jsonEmbed.put("author", jsonAuthor);
                }
                final ArrayList<JSONObject> jsonFields = new ArrayList<>();
                for (Field field : fields) {
                    JSONObject jsonField = new JSONObject();
                    jsonField.put("name", field.a());
                    jsonField.put("value", field.b());
                    jsonField.put("inline", field.c());
                    jsonFields.add(jsonField);
                }
                jsonEmbed.put("fields", jsonFields.toArray());
                list.add(jsonEmbed);

            }
            JSONSerializer.put("embeds", list.toArray());
        }
        URLConnection openConnection = new URL(this.content).openConnection();
        openConnection.addRequestProperty("Content-Type", "application/json");
        openConnection.addRequestProperty("User-Agent", "YourLocalLinuxUser");
        openConnection.setDoOutput(true);
        ((HttpsURLConnection) openConnection).setRequestMethod("POST");
        OutputStream outputStream = openConnection.getOutputStream();
        outputStream.write(JSONSerializer.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
        openConnection.getInputStream().close();
        ((HttpsURLConnection) openConnection).disconnect();
    }


    static class JSONObject {
        private final HashMap<String, Object> a;

        JSONObject() {
            this.a = new HashMap<>();
        }

        void put(final String key, final Object value) {
            if (value != null) {
                this.a.put(key, value);
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            final Set<Map.Entry<String, Object>> entrySet = this.a.entrySet();
            sb.append("{");
            int n = 0;
            for (final Map.Entry<String, Object> next : entrySet) {
                final Object value = next.getValue();
                sb.append(this.a(next.getKey())).append(":");
                if (value instanceof String) {
                    sb.append(this.a(String.valueOf(value)));
                } else if (value instanceof Integer) {
                    sb.append(Integer.valueOf(String.valueOf(value)));
                } else if (value instanceof Boolean) {
                    sb.append(value);
                } else if (value instanceof JSONObject) {
                    sb.append(value);
                } else if (value.getClass().isArray()) {
                    sb.append("[");
                    for (int length = Array.getLength(value), i = 0; i < length; ++i) {
                        final StringBuilder append = sb.append(Array.get(value, i).toString());
                        String str;
                        if (i != length - 1) {
                            str = ",";
                        } else {
                            str = "";
                        }
                        append.append(str);
                    }
                    sb.append("]");
                }
                ++n;
                sb.append(n == entrySet.size() ? "}" : ",");
            }
            return sb.toString();
        }

        private String a(final String s) {
            return "\"" + s;
        }
    }

    public static class EmbedObject {
        public String title;
        public String description;
        public String url;
        public Color color;
        public Footer footer;
        public DiscordWebhook.Thumbnail thumbnail;
        public Image image;
        public Author author;
        public final List<Field> fields;

        public EmbedObject() {
            this.fields = new ArrayList<>();
        }

        public EmbedObject setDescription(String b) {
            this.description = b;
            return this;
        }

        public EmbedObject setColor(Color d) {
            this.color = d;
            return this;
        }

        public EmbedObject setTitle(String a) {
            this.title = a;
            return this;
        }

        public EmbedObject setUrl(String c) {
            this.url = c;
            return this;
        }

        public EmbedObject setFooter(String e, String f) {
            this.footer = new Footer(e, f);
            return this;
        }

        public EmbedObject setImage(Image f) {
            this.image = f;
            return this;
        }

        public EmbedObject setThumbnail(String g) {
            this.thumbnail = new Thumbnail(g);
            return this;
        }

        public EmbedObject setAuthor(Author h) {
            this.author = h;
            return this;
        }

        public EmbedObject addField(final String a, final String b, final boolean c) {
            this.fields.add(new Field(a, b, c));
            return this;
        }

    }

    record Image(String url) {
    }

    record Footer(String text, String iconUrl) {
    }

    record Field(String a, String b, boolean c) {
    }

    record Author(String name, String url, String iconUrl) {
    }

    record Thumbnail(String url) {
    }

}
