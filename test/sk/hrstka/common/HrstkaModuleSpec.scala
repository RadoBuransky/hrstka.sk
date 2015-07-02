package sk.hrstka.common

import com.google.inject.Binder
import com.google.inject.binder.{AnnotatedBindingBuilder, ScopedBindingBuilder}
import org.mockito.Mockito._
import play.api.cache.CacheApi
import sk.hrstka.common.impl.{DefaultEhHrstkaCache, HrstkaEhHrstkaCache}
import sk.hrstka.test.BaseSpec

import scala.reflect.{ClassTag, _}

class HrstkaModuleSpec extends BaseSpec {
  behavior of "configure"

  it should "setup all bindings" in new TestScope {
    verifyBinding[ApplicationLifecycle, ApplicationLifecycleImpl] {
      verifyBinding[CacheApi, DefaultEhHrstkaCache] {
        verifyBinding[HrstkaCache, HrstkaEhHrstkaCache] {
          hrstkaModule.configure(binder)
        }
      }
    }
  }

  private class TestScope {
    val binder = mock[Binder]
    val hrstkaModule = new HrstkaModule()

    def verifyBinding[T : ClassTag, TImpl <: T : ClassTag](action: => Unit)(implicit ev: Manifest[AnnotatedBindingBuilder[T]]): Unit = {
      val tClass = classTag[T].runtimeClass.asInstanceOf[Class[T]]
      val tImplClass = classTag[TImpl].runtimeClass.asInstanceOf[Class[TImpl]]

      // Prepare
      val bindingBuilder = mock[AnnotatedBindingBuilder[T]]
      when(binder.bind(tClass))
        .thenReturn(bindingBuilder)
      val scopedBindingBuilder = mock[ScopedBindingBuilder]
      when(bindingBuilder.to(tImplClass))
        .thenReturn(scopedBindingBuilder)

      // Execute
      action

      // Verify
      verify(bindingBuilder).to(tImplClass)
      verify(binder).bind(classOf[ApplicationLifecycle])
    }
  }
}
