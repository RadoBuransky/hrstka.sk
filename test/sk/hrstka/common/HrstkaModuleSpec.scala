package sk.hrstka.common

import com.google.inject.Binder
import com.google.inject.binder.{AnnotatedBindingBuilder, ScopedBindingBuilder}
import org.mockito.Mockito._
import sk.hrstka.test.BaseSpec

import scala.reflect._
import scala.reflect.ClassTag

class HrstkaModuleSpec extends BaseSpec {
  behavior of "configure"

  it should "bind ApplicationLifecycle to ApplicationLifecycleImpl as eager singleton" in new TestScope {
    verifyBinding[ApplicationLifecycle, ApplicationLifecycleImpl]()
  }

  private class TestScope {
    val binder = mock[Binder]
    val hrstkaModule = new HrstkaModule()

    def verifyBinding[T : ClassTag, TImpl <: T : ClassTag]()(implicit ev: Manifest[AnnotatedBindingBuilder[T]]): Unit = {
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
      hrstkaModule.configure(binder)

      // Verify
      verify(scopedBindingBuilder).asEagerSingleton()
      verify(bindingBuilder).to(tImplClass)
      verify(binder).bind(classOf[ApplicationLifecycle])

    }
  }
}
