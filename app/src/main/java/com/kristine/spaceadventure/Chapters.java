package com.kristine.spaceadventure;

import com.google.firebase.database.IgnoreExtraProperties;

/** Glabājas truktūra nodaāli (chapter) */
@IgnoreExtraProperties
public class Chapters {
    private String plot;
    private boolean options;
    private String image_url;
    private OptionText option_text;

    public Chapters(){}

    public Chapters(String plot, boolean options, String image_url, OptionText option_text) {
        this.plot = plot;
        this.options = options;
        this.image_url = image_url;
        this.option_text = option_text;
    }

    public String getPlot() {
        return plot;
    }

    public boolean isOptions() {
        return options;
    }

    public String getImage_url() {
        return image_url;
    }

    public OptionText getOption_text() {
        return option_text;
    }
}


@IgnoreExtraProperties
class OptionText {
    private String option_first;
    private String option_second;
    private OptionStory option_story;

    public OptionText() {}

    public OptionText(String option_first, String option_second, OptionStory option_story) {
        this.option_first = option_first;
        this.option_second = option_second;
        this.option_story = option_story;
    }

    public String getOption_first() {
        return option_first;
    }

    public String getOption_second() {
        return option_second;
    }

    public OptionStory getOption_story() {
        return option_story;
    }
}

@IgnoreExtraProperties
class OptionStory {
    private String story_first;
    private String story_second;

    public OptionStory(){}

    public OptionStory(String story_first, String story_second) {
        this.story_first = story_first;
        this.story_second = story_second;
    }

    public String getStory_first() {
        return story_first;
    }

    public String getStory_second() {
        return story_second;
    }
}