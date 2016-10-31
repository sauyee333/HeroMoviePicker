package com.sauyee333.herospin.listener;

import com.sauyee333.herospin.network.marvel.model.characterList.Results;

/**
 * Created by sauyee on 31/10/16.
 */

public interface HeroCharacterListener {
    void onCharacterClick(Results results);
}
