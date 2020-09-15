package com.sfmap.library.container;

/**
 * 用来控制NodeFragment的运行模式，LaunchMode的种类与特性与Activity的LaunchMode基本相同。<br/>
 * 具体的特性详见不同LaunchMode的说明。
 */
public abstract class LaunchMode {

    /**
     * @deprecated
     * 与Activity的SingleTop特性相同。<br/>
     * 如果当前栈顶的Fragment与要启动的Fragment相同则重用当前Fragment, 并通过
     * {@linkplain NodeFragment#onNewNodeFragmentBundle(NodeFragmentBundle)}
     * 传递新数据，否则创建新的实例。
     */
    public static interface launchModeSingleTop {

    }

    /**
     * @deprecated
     * 与Activity的SingleTask特性相同。<br/>
     * 如果当前栈中包含与要启动的Fragment类型相同的实例则重用该Fragment,该Fragment之上所有的Fragment清除出栈， 并通过
     * {@linkplain NodeFragment#onNewNodeFragmentBundle(NodeFragmentBundle)}
     * 传递新数据，否则创建新的实例。
     */
    public static interface launchModeSingleTask {

    }

    /**
     * @deprecated
     */
    public static interface launchModeSingleTaskWithoutReuse extends
	    launchModeSingleTask {
    }

    /**
     * @deprecated
     * 与Activity的SingleIntance特性类似，但是并不会启动新的Fragment堆栈。<br/>
     * 与{@linkplain launchModeSingleTask}
     * 的区别为SingleIntance并不会把该Fragment之上的其他Fragment从栈中清除。<br/>
     * 如果当前栈中包含与要启动的Fragment类型相同的实例则重用该Fragment， 并通过
     * {@linkplain NodeFragment#onNewNodeFragmentBundle(NodeFragmentBundle)}
     * 传递新数据，否则创建新的实例。
     */
    public static interface launchModeSingleInstance {
    }

    /**
     * @deprecated
     */
    public static interface launchModeSingleIntanceWithoutReuse extends
	    launchModeSingleInstance {
    }

    /**
     * 根据产品需求，Poi详情页特有的运行模式，其他页面不要使用。<br/>
     * Poi详情页需要同时implements该LaunchMode及{@linkplain launchModeSingleInstance}
     * ,以便整合SingleInstance和SingleTop各自的部分特性：<br/>
     * 1、如果当前栈顶与要启动的页面类型相同，则判断是否超过了最大重复上限（上限通过{@linkplain #maxDuplicateCount()}
     * 获取）， 如果没有超过则再启动一个新的页面，超过则销毁最早启动的同类型页面，保持总数与maxDuplicateCount相等。<br/>
     * 2、如果当前栈顶与要启动的页面类型不同则检查栈内是否包含该类型页面，如果包含则采用SingleInstance的策略将其从栈内清除。
     */
    public static interface launchModeSingleTopAllowDuplicate {
	/**
	 * 相同页面在栈顶允许保持的最大重复数
	 * 
	 * @return 最大重复数
	 */
	int maxDuplicateCount();
    }
}
