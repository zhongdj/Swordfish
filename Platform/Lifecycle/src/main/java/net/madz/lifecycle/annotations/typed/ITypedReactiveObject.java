package net.madz.lifecycle.annotations.typed;

/**
 * A reactive object is one whose behavior is best characterized by its response
 * to events dispatched from outside its context.
 * 
 * A reactive object has a clear lifetime whose current behavior is affected by
 * its past.
 * 
 * For more information please refer to: http://umlguide2.uw.hu/ch25.html
 * 
 * @author barry
 * 
 */
public interface ITypedReactiveObject {

    @SuppressWarnings("rawtypes")
    <S extends ITypedState> S getState();

}
