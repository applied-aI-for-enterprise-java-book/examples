package org.acme;

import dev.langchain4j.classification.EmbeddingModelTextClassifier;
import dev.langchain4j.classification.TextClassifier;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class MainApp {

    public enum SentimentCategory {
        ANGRY,
        JOY,
        ANXIETY,
        DISGUST,
        FEAR,
        SADNESS,
        ENVY,
        ENNUI,
        EMBARRASSMENT
    }

    static Map<SentimentCategory, List<String>> examples = new HashMap<>();

    static {
        examples.put(SentimentCategory.ANGRY, asList(
                "when I heard the news, I was really angry",
                "her skin was splotched with angry red burns",
                "why are you angry with me?",
                "you're not angry with me, are you?",
                "I'm angry that she didn't call me",
                "she got angry when I said I hadn't done the work",
                "I'm really angry with him",
                "the wild, angry sea",
                "she always gets angry if she doesn't get her own way",
                "the bruise below his eye looked angry and sore",
                "I can't believe you did that!",
                "This is absolutely unacceptable!",
                "How many times do I have to tell you?",
                "I'm so fed up with this nonsense!",
                "You have no right to talk to me like that!",
                "This is the last straw!",
                "Why do you always make things so difficult?",
                "I am furious about your behavior!",
                "I won't tolerate this any longer!",
                "You have completely crossed the line!"
        ));

        examples.put(SentimentCategory.JOY, asList(
                "her dancing is a joy to watch",
                "he took great joy in painting",
                "he was jumping for joy",
                "I had the joy of listening to her sing",
                "she brought joy into people's lives",
                "I was filled with joy at the prospect of seeing him again",
                "the joy of this film is in the dialogue",
                "the joy of being alive",
                "And then I discovered the joy of running through a forest, and was spoilt forever.",
                "I felt a surge of joy when I saw my best friend after a long time.",
                "Winning the competition filled me with immense happiness.",
                "The children's laughter brought a smile to everyone's face.",
                "Her eyes sparkled with joy as she unwrapped her birthday present.",
                "The beautiful sunset over the ocean left me feeling euphoric.",
                "He couldn't contain his joy when he heard the good news.",
                "The reunion with my family after months apart was incredibly joyous.",
                "Seeing the surprise party made her jump with joy.",
                "The little puppy wagged its tail in pure joy when it saw its owner.",
                "The success of our project brought us all great joy and satisfaction."
        ));

        examples.put(SentimentCategory.ANXIETY, asList(
                "she was in a state of anxiety",
                "he felt a surge of anxiety",
                "he feels great anxiety for his son in the jungle",
                "in his anxiety to help, he actually made things worse",
                "Achievements since are the result of an anxiety to play it safe.",
                "it was a pathetic anxiety to please",
                "They do not seem to know that depression and anxiety can cause eating disorders.",
                "the news only increased my anxiety",
                "we are seeing more calls related to anxiety and depression",
                "the move was driven by the government's anxiety to avoid another scandal",
                "I can't stop worrying about the upcoming exam.",
                "The thought of speaking in public makes my heart race.",
                "Every time I get an email from my boss, I feel a knot in my stomach.",
                "I constantly fear that something bad is going to happen.",
                "I feel tense and on edge all the time, even without a clear reason.",
                "The idea of being in a crowded place makes me very anxious.",
                "I'm always second-guessing my decisions, afraid I've made a mistake.",
                "Meeting new people gives me intense feelings of anxiety.",
                "I often wake up in the middle of the night, feeling panicked about the future.",
                "The pressure to perform well at work is overwhelming and stressful."
        ));

        examples.put(SentimentCategory.DISGUST, asList(
                "the sight filled her with disgust",
                "at this point I feel nothing for them but disgust",
                "The cabin smelled of mildew, and they turned their noses up in disgust.",
                "some of the audience walked out in disgust",
                "I write to you in disgust at the comments made by your columnist.",
                "I felt a mixture of disgust and pity",
                "She almost pulled her hand away in disgust, but managed to control herself.",
                "Some small shareholders were so upset by events they walked out in disgust.",
                "When the motion was passed over 200 delegates stormed out of the conference in disgust.",
                "Sarah looked away in disgust, but everywhere were the signs of disrepair.",
                "The smell coming from the trash was absolutely revolting.",
                "I felt a wave of disgust wash over me when I saw the dirty kitchen.",
                "The sight of the moldy bread made my stomach churn.",
                "She recoiled in disgust at the sight of the rotting fruit.",
                "He couldn't hide his disgust when he saw the mess left behind.",
                "The texture of the slimy food made me gag in disgust.",
                "I was disgusted by the filthy conditions in the public restroom.",
                "The thought of eating insects fills me with disgust.",
                "The sight of the overflowing garbage bin made me wrinkle my nose in disgust.",
                "I felt a surge of disgust as I watched the repulsive scene unfold."
        ));

        examples.put(SentimentCategory.FEAR, asList(
                "he showed no fear",
                "he did it out of fear",
                "the love and fear of God",
                "I fear that she may be dead",
                "they fled the city in fear of their lives",
                "there's no fear of him being late",
                "the news struck fear into his heart",
                "We fear to leave and enter the houses at the front doors, and guess what?",
                "I fear for the city with this madman let loose in it",
                "I shall buy her book, though not, I fear, the hardback version",
                "The sound of footsteps behind him in the dark alley filled him with fear.",
                "She had a fear of heights and couldn't bring herself to look down from the balcony.",
                "His fear of public speaking caused him to stutter during his presentation.",
                "The fear of losing his job kept him awake at night.",
                "She couldn't shake the fear of something lurking under her bed.",
                "The news of the approaching storm filled the town with fear.",
                "He had a fear of flying and always felt anxious before a flight.",
                "The fear of failure prevented her from starting her own business.",
                "The eerie silence in the abandoned house sent chills down his spine.",
                "She had a fear of spiders and screamed at the sight of one in her room."
        ));

        examples.put(SentimentCategory.SADNESS, asList(

                "a source of great sadness",
                "he was overcome by a feeling of sadness",
                "there was much sadness in the community after she died",
                "However, my delight at the demise of the Western bypass is tinged with sadness.",
                "I feel a sense of deep sadness, verging on depression, about the situation in Asia.",
                "She felt a deep sadness as she watched the rain fall outside.",
                "His heart ached with the sorrow of losing his best friend.",
                "Tears welled up in her eyes as she read the farewell letter.",
                "He couldn't shake off the overwhelming feeling of loneliness.",
                "The house felt empty and quiet after the children moved away.",
                "A sense of hopelessness enveloped her after the news of the tragedy.",
                "He sat by the window, reminiscing about the happier times now gone.",
                "She struggled to find joy in things that used to make her happy.",
                "The loss of his job left him feeling worthless and defeated.",
                "Her heart broke every time she thought about the past."
        ));

        examples.put(SentimentCategory.ENVY, asList(
                "she felt a twinge of envy for the people on board",
                "I envy you - I never get a day off",
                "they stared at her purchases with envy",
                "it's unfair to envy him his good fortune",
                "a lifestyle which most of us would envy",
                "France has a film industry that is the envy of Europe",
                "They were all consumed by envy now on top of the original dislike.",
                "I envy Jane her happiness",
                "I always envy other people's enjoyment of London Town.",
                "his win caused considerable envy among his colleagues",
                "She felt a pang of envy when she saw her friend's brand-new car.",
                "John couldn't hide his envy as he watched his colleague receive the promotion he wanted.",
                "The envy was clear in her eyes as she looked at the couple's luxurious vacation photos.",
                "He envied his neighbor's beautiful garden, wishing his own looked as nice.",
                "Her envy grew as she listened to her classmates discuss their exciting summer plans.",
                "Tom felt a surge of envy when he saw his brother's impressive collection of trophies.",
                "She couldn't help but envy the effortless way her friend charmed everyone at the party.",
                "His envy was palpable when he learned that his best friend was getting married before him.",
                "She tried to hide her envy as her coworker was praised for a job well done.",
                "Seeing his friend's new house filled him with envy, as he still lived in a small apartment."
        ));

        examples.put(SentimentCategory.ENNUI, asList(
                "he succumbed to ennui and despair",
                "Personally, I found it quite dull, but then I suffer from financial ennui.",
                "Temporary ennui or a more long-term change of direction?",
                "He stared out the window, feeling a deep sense of ennui as the rain drizzled down the glass.",
                "The endless routine of his daily commute filled him with a growing ennui.",
                "She couldn't shake the ennui that settled over her as she scrolled through her phone.",
                "Despite the excitement around him, he felt a profound ennui that he couldn't explain.",
                "The dullness of the meeting led to a palpable ennui in the room.",
                "With nothing to do and nowhere to go, she felt a heavy ennui wash over her.",
                "The novel's protagonist struggled with a pervasive sense of ennui throughout the story.",
                "His ennui was evident in the way he listlessly moved from one task to another.",
                "She felt a wave of ennui as she listened to the monotonous lecture.",
                "Even in the midst of the party, an overwhelming ennui kept him detached and uninterested."
        ));

        examples.put(SentimentCategory.EMBARRASSMENT, asList(
                "to my embarrassment they told everyone what I'd done",
                "her extreme views might be an embarrassment to the movement",
                "to hide my embarrassment I turned away",
                "I turned red with embarrassment",
                "he was an embarrassment who was safely left ignored",
                "It will be profound, a mixture of embarrassment, shame and a huge feeling of emptiness.",
                "I felt no embarrassment about it",
                "you're an embarrassment to your colleagues and to the profession",
                "He cannot say he was unprepared for York council's financial embarrassment.",
                "Today I have no pride, no faith, only embarrassment, anger, and frustration.",
                "I tripped and fell in front of everyone during the presentation.",
                "She realized she had been walking around all day with her shirt inside out.",
                "He called his boss 'mom' by accident in the middle of a meeting.",
                "I accidentally replied to the wrong email chain and shared a personal joke with the whole office.",
                "She forgot the lyrics while singing on stage and had to hum the rest of the song.",
                "He waved enthusiastically at someone who turned out to be a complete stranger.",
                "I spilled my drink all over myself at a fancy dinner party.",
                "She mispronounced a common word repeatedly during her speech.",
                "He sent a text meant for his friend to his crush instead.",
                "I got caught singing loudly to my favorite song while wearing headphones in public."
        ));

    }

    public static void main(String[] args) {

        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        TextClassifier<SentimentCategory> classifier =
                new EmbeddingModelTextClassifier<>(embeddingModel, examples);
        System.out.println(
                classifier
                        .classify("I don't want to hear anyone, I want to live alone")
        );
    }
}
