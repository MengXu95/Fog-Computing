package mengxu.feature;


import mengxu.feature.ignore.Ignorer;

/**
 * Created by yimei on 12/10/16.
 */
public interface FeatureIgnorable {

    Ignorer getIgnorer();
    void setIgnorer(Ignorer ignorer);
}
