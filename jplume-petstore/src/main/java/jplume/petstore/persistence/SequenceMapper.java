package jplume.petstore.persistence;

import jplume.petstore.domain.Sequence;


public interface SequenceMapper {

  Sequence getSequence(Sequence sequence);
  void updateSequence(Sequence sequence);
}
