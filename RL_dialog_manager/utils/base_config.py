class BareConfig:

    def __init__(self):

        # Experiment configuration
        self.epochs = 10
        self.batch_size = 8
        self.test_batch_size = 1
        self.max_iteration = 100
        self.lr = 0.001
        self.load = False
        self.cuda = True
        self.shuffle = True
        self.gamma = 0.999
        self.dir_checkpoint = 'checkpoints'

        # Model setting
        self.use_usersim = True
        self.num_emotion = 4
        self.num_user_action = 3
        self.max_rounds = 20
        self.update_steps = 1000
        self.action_shape = 6
        self.hidden_shape = 24

        # Path setting
        self.log_path = 'logs/log.txt'
        self.dqn_log_path = 'logs/dqn_log.txt'
        self.checkpoint_path = 'checkpoint'

    def merge_yaml(self, cfg={}):

        if 'epochs' in cfg:
            self.epochs = cfg['epochs']
        if 'batch_size' in cfg:
            self.batch_size = cfg['batch_size']
        if 'test_batch_size' in cfg:
            self.test_batch_size = cfg['test_batch_size']
        if 'max_iteration' in cfg:
            self.max_iteration = cfg['max_iteration']
        if 'lr' in cfg:
            self.lr = cfg['lr']
        if 'load' in cfg:
            self.load = cfg['load']
        if 'cuda' in cfg:
            self.cuda = cfg['cuda']
        if 'shuffle' in cfg:
            self.shuffle = cfg['shuffle']
        if 'gamma' in cfg:
            self.gamma = cfg['gamma']
        if 'dir_checkpoint' in cfg:
            self.dir_checkpoint = cfg['dir_checkpoint']

        if 'use_usersim' in cfg:
            self.use_usersim = cfg['use_usersim']
        if 'num_emotion' in cfg:
            self.num_emotion = cfg['num_emotion']
        if 'num_user_action' in cfg:
            self.num_user_action = cfg['num_user_action']
        if 'max_rounds' in cfg:
            self.max_rounds = cfg['max_rounds']
        if 'update_steps' in cfg:
            self.update_steps = cfg['update_steps']
        if 'action_shape' in cfg:
            self.action_shape = cfg['action_shape']
        if 'hidden_shape' in cfg:
            self.hidden_shape = cfg['hidden_shape']

        if 'log_path' in cfg:
            self.log_path = cfg['log_path']
        if 'dqn_log_path' in cfg:
            self.dqn_log_path = cfg['dqn_log_path']
        if 'checkpoint_path' in cfg:
            self.checkpoint_path = cfg['checkpoint_path']
